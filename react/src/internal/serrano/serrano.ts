


import {Serrano,Include } from "../../../../java/src/main/resources/typeScript/serrano"
 

let include = Include; // apenas para incluir o arquivo na compilacao




 
let windowAny: any = window;


let servicesInstance:Map<string,any> = new Map();
 

function getSerrano() :Serrano {
  return  windowAny.serrano;
}


async function execService(beanName:string,className:string,method:string,...args:any):Promise<any>{


  let instanceName = `${beanName}${className}`
  let service = servicesInstance.get(instanceName)

  if(service == null){
    service = {}
    await  getSerrano().inject(beanName,className,service);
  }
  return  service[method](...args);
}
 

function isAlive(): Promise<boolean> {

  return new Promise((resolve) => {
    (window as any).ipc({
      request: "isAlive",
      onSuccess: function (response: string) {
        resolve(response == 'true');
      },

    });
  })

}


export { isAlive, execService, getSerrano };
