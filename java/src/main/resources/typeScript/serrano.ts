
/**
 *
 * NÃO EXPORTE CLASSES NESTE ARQUIVO
 */

export interface Serrano {
    requests: Map<string, MetadataRequest>;
    objetos: any;
    IPC_PROMISE_HANDLERS: MetadataRequest[];

    registerRequest(uuid: string, resolve: Function, reject: Function): void;
    exec(): any;
    execFunc(): any;
    finalizeObject(uuid: string): void;
    clearCollection(identificador: string): void;
    getCollectionSize(id: string): number;
    isCollectionEmpty(id: string): boolean;
    isCollectionInclude(id: string, o: any): boolean;
    addToCollection(id: string, o: any): boolean;
    removeFromCollection(id: string, o: any): boolean;
    removeFromCollectionIndex(id: string, index: number): any;
    indexOfCollection(identificador: string, o: any): number;
    lastIndexOfCollection(identificador: string, o: any): number;
    subListCollection(identificador: string, fromIndex: number, toIndex: number, identificadorLista: string): any[];
    hasNextInCollection(id: string, ponteiro: number): boolean;
    getInCollection(id: string, ponteiro: number): any;
    addAllToCollection(identificador: string, index: number, c: any[]): boolean;
    setInCollection(identificador: string, index: number, element: any): any;
    doRequest(beanName: string,beanClassName :string,methodName: string, parametersType: string[], parameters: any[], uuid: string, resolve: Function, reject: Function,register:boolean): void;
    resolveRequests(uuid: string, response: string, error: boolean): void;
    resolveResponse(response: string, rejected: boolean): any;
    isPrimitive(v: any): boolean;
    getType(v: any): string;
    resolveHandlerCall(response: IPCHandlerResponse): void;
    isPromise(p: any): boolean;
    inject(beanName: string,className:string, target: any): Promise<void>;
    objectToString(uuid: string): string
}
class SerranoImpl implements Serrano {


    requests: Map<string, MetadataRequest> = new Map()
    objetos: any= {}
    IPC_PROMISE_HANDLERS: MetadataRequest[] = []
    objectToString(uuid: string): string {
        return JSON.stringify(this.objetos[uuid],this.removeCircularReferences());
    }
     removeCircularReferences() {
        const seen = new WeakSet();
        return function(key:any,value:any) {
          if (typeof value === "object" && value !== null) {
            if (seen.has(value)) {
              // Remove a referência circular completamente
              return undefined;
            }
            seen.add(value);
          }
          return value;
        };
      }

    exec() {

        let id = arguments[0]

        let nome = arguments[1];

        let params = arguments[2]
        if (params == undefined)
            params = []
        let obj = this.objetos[id];

        let prop = obj[nome]

        if (typeof prop === 'function') {
            return prop.call(obj,...params);
        } else if (prop != undefined) {

            return prop;
        }

        if (nome.startsWith('get')) {

            nome = nome.substring(3);
            return obj[nome];

        } else if (nome.startsWith('set')) {

            nome = nome.substring(3);
            prop[nome] = params[0];
        }
        return undefined;
    }

    execFunc() {

        let id = arguments[0]
        let params = arguments[1];

        let func = this.objetos[id];

        let retorno = func(...params);
        return retorno
    }

    finalizeObject(uuid: string) {
        delete this.objetos[uuid];
    }
    clearCollection(identificador: string) {
        this.objetos[identificador]= [];
    }
    getCollectionSize(id: string) {
        return this.objetos[id].length;
    }
    isCollectionEmpty(id: string) {
        return this.objetos[id].length == 0;
    }
    isCollectionInclude(id: string, o: any) {
        return this.objetos[id].includes(o);
    }
    addToCollection(id: string, o: any) {
        let length = this.objetos[id].push(o);
        return length != -1;
    }
    removeFromCollection(id: string, o: any) {
        let collection = this.objetos[id];
        collection.splice(collection.indexOf(o), 1)
        return true;
    }
    removeFromCollectionIndex(id: string, index: number) {

        let collection = this.objetos[id];
        let item: any = collection[index];
        collection.splice(index, 1)
        return item;
    }
    indexOfCollection(identificador: string, o: any) {
        return this.objetos[identificador].indexOf(o);
    }
    lastIndexOfCollection(identificador: string, o: any) {
        return this.objetos[identificador].lastIndexOf(o);
    }
    subListCollection(identificador: string, fromIndex: number, toIndex: number, identificadorLista: string) {
        let collection = this.objetos[identificador];
        let newCollection = collection.slice(fromIndex, toIndex);
        this.objetos[identificadorLista]= newCollection;
        return newCollection;
    }

    hasNextInCollection(id: string, ponteiro: number) {
        return this.objetos[id].length > ponteiro && ponteiro > -1
    }
    getInCollection(id: string, ponteiro: number) {
        return this.objetos[id][ponteiro];
    }
    addAllToCollection(identificador: string, index: number, c: any[]) {
        let arr = this.objetos[identificador];
        let arr1 = arr.slice(0, index);
        arr1.push(...c)
        let arr2 = arr.slice(index);
        arr1.push(...arr2);
        this.objetos[identificador]= arr1;
        return true;
    }
    setInCollection(identificador: string, index: number, element: any) {
        let arr = this.objetos[identificador];
        let el = arr[index];
        arr[index] = element;
        return el;
    }

    registerRequest(uuid: string, resolve: Function, reject: Function) {
        let meta = new MetadataRequestImpl();
        meta.__promiseResolve__ = resolve;
        meta.__promiseReject__ = reject
        this.requests.set(uuid,meta);
    }
    doRequest(beanName: string,beanClassName:string, methodName: string, parametersType: string[], parameters: any[], uuid: string, resolve: Function, reject: Function,register : boolean) {
        //CASO MODIFIQUE ESTE METODO MODIFIQUE TAMBEM O ARQUIVO load.component.ts LA É CONFIGURADO O HOTRELOAD e o arquivo functionCallTemplate.js

        if(register)
            this.registerRequest(uuid, resolve, reject)
        let request = new IPCCallRequest(beanName,beanClassName, methodName, parametersType, parameters, uuid);

        let jsonString = JSON.stringify(request);
        (window as any).ipc(
            {
                request: jsonString
            }
        )

    }

    resolveRequests(uuid: string, response: string, error: boolean) {

        let request = this.requests.get(uuid);
        this.requests.delete(uuid);
        let retorno = this.resolveResponse(response, error);

        if (!error)
            request.__promiseResolve__(retorno)
        else
            request.__promiseReject__(retorno)
    }

    resolveResponse(response: string, rejected: boolean) {
        if (!rejected) {
            let resposta: IPCHandlerResponse = JSON.parse(response);
            return resposta.value;
        }
        let error;


        error = JSON.parse(response);
        return new IPCException(error.className, error.message, error.code);

    }
    isPrimitive(v: any) {
        return typeof v === 'number' || v instanceof Date || typeof v === 'string' || typeof v === 'boolean' || typeof v === 'bigint' || typeof v === "symbol";
    }

    getType(v: any) {

        if (v === undefined)
            return 'UNDEFINED';
        else if (typeof v === 'string' || typeof v === 'symbol')
            return 'STRING';
        else if (typeof v === 'number')
            return 'NUMBER'
        else if (typeof v === 'bigint')
            return 'NUMBER'
        else if (typeof v === 'boolean')
            return 'BOOLEAN';
        else if (typeof v === 'function')
            return 'FUNCAO';
        else if (v instanceof Array)
            return 'ARRAY'
        else
            return "OBJECT";
    }
    resolveHandlerCall(response: IPCHandlerResponse) {
        this.IPC_PROMISE_HANDLERS.filter((h: MetadataRequest) => h.__methodInfo__.beanName == response.beanName && h.__methodInfo__.method == response.methodName)
            .forEach((h: MetadataRequest) => {
                this.IPC_PROMISE_HANDLERS.splice(this.IPC_PROMISE_HANDLERS.indexOf(h), 1);
                Promise.resolve().then(() => {
                    if (response.hasError)
                        h.__promiseReject__(response.value);
                    else
                        h.__promiseResolve__(response.value);
                });

            });

    }
    isPromise = function isPromise(p: any) {
        return p && Object.prototype.toString.call(p) === "[object Promise]";
    }

    private createMethod(methodInfo: MethodInfo): any {

        let serrano = this;

        function normalCall() {
            let args = Array.from(arguments);

            let metadado: MetadataRequest = (args != undefined && args.length > 0 && args.at(-1) != undefined && (args.at(-1) as MetadataRequest).__isMetadado__) ? args.at(-1) : undefined;

            if (metadado) {
                args.pop();
            } else {
                metadado = new MetadataRequestImpl();
            }

            return new Promise((resolve, reject) => {
                metadado.__promiseReject__ = reject;
                metadado.__promiseResolve__ = resolve;
                serrano.doRequest(methodInfo.beanName, methodInfo.beanClassName, methodInfo.method, methodInfo.paramsClass, args, crypto.randomUUID(), resolve, reject,true);
            })

        }

        function handlerCall() {
            let metadado: MetadataRequest = new MetadataRequestImpl();

            metadado.__methodInfo__ = methodInfo;

            let promise = new Promise((resolve, reject) => {
                metadado.__promiseReject__ = reject;
                metadado.__promiseResolve__ = resolve;
                serrano.IPC_PROMISE_HANDLERS.push(
                    metadado
                );

            });

            return promise
        }

        return methodInfo.isPromise ? handlerCall : normalCall;

    }

    public async inject(beanName: string,className:string, target: any): Promise<void> {
        try {

            let methodInfos: MethodInfo[] = await new Promise(
                (resolve, reject) => {
                    this.doRequest("iPC","ipc.base.IPC","getBeanInfo", ["java.lang.String","java.lang.String"], [beanName,className], "_INJECTION" + crypto.randomUUID() + "INJECTION__", resolve, reject,true);
                }
            );


            for (let methodInfo of methodInfos) {
                target[methodInfo.method] = this.createMethod(methodInfo);
            }


        } catch (e) {
            throw e;
        }
    }

}





class IPCHandlerResponse {
    constructor(public readonly methodName: string, public readonly beanName: string, public readonly value: any, public readonly hasError: boolean) { }

}

interface MetadataRequest {
    __progressFunc__: (ev: ProgressEvent) => void;
    __promiseResolve__: Function;
    __promiseReject__: Function;
    __ignoreToken__: boolean;
    __isMetadado__: boolean;
    __forcarExecucaoWS__: boolean;
    __methodInfo__: MethodInfo;
}

class MetadataRequestImpl implements MetadataRequest {
    public __progressFunc__: (ev: ProgressEvent) => void;
    public __promiseResolve__: Function;
    public __promiseReject__: Function;
    public __ignoreToken__: boolean
    public __isMetadado__: boolean = true;
    public __forcarExecucaoWS__: boolean = false
    public __methodInfo__: MethodInfo;
}
interface MethodInfo {
    method: string;
    beanName: string;
    isPromise: boolean;
    beanClassName:string;
    paramsClass: string[];
}
class MethodInfoImpl {
    public method: string;
    public beanName: string;
    public isPromise: boolean;
    public paramsClass: string[];
}

export class IPCCallRequest {
    constructor(public readonly beanName: string, public readonly beanClassName:string ,public readonly methodName: string, public readonly parametersType: string[],

        public readonly parameters: any[], public readonly uuid: string
    ) { }



}


class IPCException {
    constructor(public className: string, public menssagem: string, public code: number) {

    }
}


(window as any).serrano = new SerranoImpl();


