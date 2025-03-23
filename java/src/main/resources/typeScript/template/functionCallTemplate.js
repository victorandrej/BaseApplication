(function () {

    let doRequest = (id,value,hasError)=>{

        if(hasError && !serrano.isPrimitive(value))
            value = JSON.stringify(value);

        let type =  serrano.getType(value);
        if(!hasError && value != undefined && !serrano.isPrimitive(value)){
            window.serrano.objetos[id] =  value;
            value = undefined;
        }

        serrano.doRequest('javaScriptService','base.web.javascript.JavaScriptService','resolve',[ 'base.web.javascript.JSRetorno', 'java.lang.Boolean'],
            [{ type: type ,value: value ,identificador: id} , hasError],undefined,undefined,undefined,false
        );

   }

    let hasError = false;
    let retorno = undefined;
    let identificador  = __$$IDENTIFICADOR$$__
    try {
        retorno = __$$FUNCTION$$__;

    } catch (e) {
        console.log(e);
        retorno = e
        hasError = true
    }

    if(window.serrano.isPromise(retorno)){
        retorno.then((v)=>{
            doRequest(identificador,v,false)
        }).catch(err =>{
                   console.log(err);
                   doRequest(identificador, err, true)
                   });
    }else{
        doRequest(identificador,retorno,hasError)
    }

}
)();


