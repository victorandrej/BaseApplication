/**
 *
 * NÃO EXPORTE CLASSES NESTE ARQUIVO
 */
class SerranoImpl {
    requests = new Map();
    objetos = {};
    IPC_PROMISE_HANDLERS = [];
    objectToString(uuid) {
        return JSON.stringify(this.objetos[uuid], this.removeCircularReferences());
    }
    removeCircularReferences() {
        const seen = new WeakSet();
        return function (key, value) {
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
        let id = arguments[0];
        let nome = arguments[1];
        let params = arguments[2];
        if (params == undefined)
            params = [];
        let obj = this.objetos[id];
        let prop = obj[nome];
        if (typeof prop === 'function') {
            return prop.call(obj, ...params);
        }
        else if (prop != undefined) {
            return prop;
        }
        if (nome.startsWith('get')) {
            nome = nome.substring(3);
            return obj[nome];
        }
        else if (nome.startsWith('set')) {
            nome = nome.substring(3);
            prop[nome] = params[0];
        }
        return undefined;
    }
    execFunc() {
        let id = arguments[0];
        let params = arguments[1];
        let func = this.objetos[id];
        let retorno = func(...params);
        return retorno;
    }
    finalizeObject(uuid) {
        delete this.objetos[uuid];
    }
    clearCollection(identificador) {
        this.objetos[identificador] = [];
    }
    getCollectionSize(id) {
        return this.objetos[id].length;
    }
    isCollectionEmpty(id) {
        return this.objetos[id].length == 0;
    }
    isCollectionInclude(id, o) {
        return this.objetos[id].includes(o);
    }
    addToCollection(id, o) {
        let length = this.objetos[id].push(o);
        return length != -1;
    }
    removeFromCollection(id, o) {
        let collection = this.objetos[id];
        collection.splice(collection.indexOf(o), 1);
        return true;
    }
    removeFromCollectionIndex(id, index) {
        let collection = this.objetos[id];
        let item = collection[index];
        collection.splice(index, 1);
        return item;
    }
    indexOfCollection(identificador, o) {
        return this.objetos[identificador].indexOf(o);
    }
    lastIndexOfCollection(identificador, o) {
        return this.objetos[identificador].lastIndexOf(o);
    }
    subListCollection(identificador, fromIndex, toIndex, identificadorLista) {
        let collection = this.objetos[identificador];
        let newCollection = collection.slice(fromIndex, toIndex);
        this.objetos[identificadorLista] = newCollection;
        return newCollection;
    }
    hasNextInCollection(id, ponteiro) {
        return this.objetos[id].length > ponteiro && ponteiro > -1;
    }
    getInCollection(id, ponteiro) {
        return this.objetos[id][ponteiro];
    }
    addAllToCollection(identificador, index, c) {
        let arr = this.objetos[identificador];
        let arr1 = arr.slice(0, index);
        arr1.push(...c);
        let arr2 = arr.slice(index);
        arr1.push(...arr2);
        this.objetos[identificador] = arr1;
        return true;
    }
    setInCollection(identificador, index, element) {
        let arr = this.objetos[identificador];
        let el = arr[index];
        arr[index] = element;
        return el;
    }
    registerRequest(uuid, resolve, reject) {
        let meta = new MetadataRequestImpl();
        meta.__promiseResolve__ = resolve;
        meta.__promiseReject__ = reject;
        this.requests.set(uuid, meta);
    }
    doRequest(beanName, beanClassName, methodName, parametersType, parameters, uuid, resolve, reject, register) {
        //CASO MODIFIQUE ESTE METODO MODIFIQUE TAMBEM O ARQUIVO load.component.ts LA É CONFIGURADO O HOTRELOAD e o arquivo functionCallTemplate.js
        if (register)
            this.registerRequest(uuid, resolve, reject);
        let request = new IPCCallRequest(beanName, beanClassName, methodName, parametersType, parameters, uuid);
        let jsonString = JSON.stringify(request);
        window.ipc({
            request: jsonString
        });
    }
    resolveRequests(uuid, response, error) {
        let request = this.requests.get(uuid);
        this.requests.delete(uuid);
        let retorno = this.resolveResponse(response, error);
        if (!error)
            request.__promiseResolve__(retorno);
        else
            request.__promiseReject__(retorno);
    }
    resolveResponse(response, rejected) {
        if (!rejected) {
            let resposta = JSON.parse(response);
            return resposta.value;
        }
        let error;
        error = JSON.parse(response);
        return new IPCException(error.className, error.message, error.code);
    }
    isPrimitive(v) {
        return typeof v === 'number' || v instanceof Date || typeof v === 'string' || typeof v === 'boolean' || typeof v === 'bigint' || typeof v === "symbol";
    }
    getType(v) {
        if (v === undefined)
            return 'UNDEFINED';
        else if (typeof v === 'string' || typeof v === 'symbol')
            return 'STRING';
        else if (typeof v === 'number')
            return 'NUMBER';
        else if (typeof v === 'bigint')
            return 'NUMBER';
        else if (typeof v === 'boolean')
            return 'BOOLEAN';
        else if (typeof v === 'function')
            return 'FUNCAO';
        else if (v instanceof Array)
            return 'ARRAY';
        else
            return "OBJECT";
    }
    resolveHandlerCall(response) {
        this.IPC_PROMISE_HANDLERS.filter((h) => h.__methodInfo__.beanName == response.beanName && h.__methodInfo__.method == response.methodName)
            .forEach((h) => {
            this.IPC_PROMISE_HANDLERS.splice(this.IPC_PROMISE_HANDLERS.indexOf(h), 1);
            Promise.resolve().then(() => {
                if (response.hasError)
                    h.__promiseReject__(response.value);
                else
                    h.__promiseResolve__(response.value);
            });
        });
    }
    isPromise = function isPromise(p) {
        return p && Object.prototype.toString.call(p) === "[object Promise]";
    };
    createMethod(methodInfo) {
        let serrano = this;
        function normalCall() {
            let args = Array.from(arguments);
            let metadado = (args != undefined && args.length > 0 && args.at(-1) != undefined && args.at(-1).__isMetadado__) ? args.at(-1) : undefined;
            if (metadado) {
                args.pop();
            }
            else {
                metadado = new MetadataRequestImpl();
            }
            return new Promise((resolve, reject) => {
                metadado.__promiseReject__ = reject;
                metadado.__promiseResolve__ = resolve;
                serrano.doRequest(methodInfo.beanName, methodInfo.beanClassName, methodInfo.method, methodInfo.paramsClass, args, crypto.randomUUID(), resolve, reject, true);
            });
        }
        function handlerCall() {
            let metadado = new MetadataRequestImpl();
            metadado.__methodInfo__ = methodInfo;
            let promise = new Promise((resolve, reject) => {
                metadado.__promiseReject__ = reject;
                metadado.__promiseResolve__ = resolve;
                serrano.IPC_PROMISE_HANDLERS.push(metadado);
            });
            return promise;
        }
        return methodInfo.isPromise ? handlerCall : normalCall;
    }
    async inject(beanName, className, target) {
        try {
            let methodInfos = await new Promise((resolve, reject) => {
                this.doRequest("iPC", "ipc.base.IPC", "getBeanInfo", ["java.lang.String", "java.lang.String"], [beanName, className], "_INJECTION" + crypto.randomUUID() + "INJECTION__", resolve, reject, true);
            });
            for (let methodInfo of methodInfos) {
                target[methodInfo.method] = this.createMethod(methodInfo);
            }
        }
        catch (e) {
            throw e;
        }
    }
}
class IPCHandlerResponse {
    methodName;
    beanName;
    value;
    hasError;
    constructor(methodName, beanName, value, hasError) {
        this.methodName = methodName;
        this.beanName = beanName;
        this.value = value;
        this.hasError = hasError;
    }
}
class MetadataRequestImpl {
    __progressFunc__;
    __promiseResolve__;
    __promiseReject__;
    __ignoreToken__;
    __isMetadado__ = true;
    __forcarExecucaoWS__ = false;
    __methodInfo__;
}
class MethodInfoImpl {
    method;
    beanName;
    isPromise;
    paramsClass;
}
 class IPCCallRequest {
    beanName;
    beanClassName;
    methodName;
    parametersType;
    parameters;
    uuid;
    constructor(beanName, beanClassName, methodName, parametersType, parameters, uuid) {
        this.beanName = beanName;
        this.beanClassName = beanClassName;
        this.methodName = methodName;
        this.parametersType = parametersType;
        this.parameters = parameters;
        this.uuid = uuid;
    }
}
class IPCException {
    className;
    menssagem;
    code;
    constructor(className, menssagem, code) {
        this.className = className;
        this.menssagem = menssagem;
        this.code = code;
    }
}
window.serrano = new SerranoImpl();
