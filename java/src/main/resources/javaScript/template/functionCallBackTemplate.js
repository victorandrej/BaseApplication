function __GENERICFUNC__() {
    var serrano = window.serrano;
    var args = [];
    var identificador = __$$IDENTIFICADOR$$__;
    for (var i = 0; i < arguments.length; i++) {
        var parametro = arguments[i];
        var parameterID = '__PARAMETER' + crypto.randomUUID() + 'PARAMETER__';
        if (parametro != undefined && !serrano.isPrimitive(parametro)) {
            serrano.objetos[parameterID] = parametro;
        }
        args.push({ type: serrano.getType(parametro), value: parametro, identificador: parameterID });
    }
    return new Promise(function (resolve, reject) {
        serrano.doRequest('javaScriptService', 'com.teresoft.serrano.web.javascript.JavaScriptService', 'resolveCallBack', ['java.lang.String', 'java.util.List'], [identificador, args], "__CALLBACK" + crypto.randomUUID() + "CALLBACK__", resolve, reject, true);
    });
}
