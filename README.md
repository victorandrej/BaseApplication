# Base Application

Aplicativo base para criacao de apps usando react e jcef ja configurado.


para utilizar um bean no contexto do ioc utilize a anotacao io.github.victorandrej.tinyioc.Bean

para chamada de um servico utilize execService  do arquivo react/src/internal/serrano/serrano.ts;

Assinatura
execService(beanName:string,className:string,method:string,...args:any):Promise<any>

para registro de uma promise que possa ser chamada pelo java utilize
execService(beanName:string,className:string,method:string):Promise<any>

para permitir que um bean funcione como servico utilize a anotacao base.ipc.Service na classe

para permitir um metodo utilize a anotacao base.ipc.Export no metodo


para criar filtros sobre a execucao de um servico utilize  base.ipc.IpcChain



Antes de compilar instale as dependencias python do requiriments.txt
para compilar utilize  "py ./Compile.py"
os arquivos compilados ficaram disponiveis em ./distribuicao

para utilizar um objeto javascript no java crie um facade utilizando o bean base.web.JavaScript 
assinatura facade(Class<T> c, String name, CefBrowser browser)
a maneira mais sim simples e colocando o objeto no window e chamando no java: facade(interface,"window.foo",browserAtual)

para utilizar uma funcao lambda em uma facade, apena utilize uma interface funcional, como java.lang.Runnable
ex:

interface Foo{

    void doSomething(Runnable calback);

}

Foo foo = javascript.facade(Foo.class,"window.foo",currentBrowser);


foo.doSomething(()->{
    System.out.println("callback")
})


o callback tambem suporta parametros e outros callbacks
ex:


interface Foo{
    void doSomething(Foo f)
}

para injetar um script utilize o metodo injectScript(String script, CefBrowser browser)

para cria um callback para o java no scrip utilize String callback(Object o, CefBrowser browser),  isso retornara uma funcao js