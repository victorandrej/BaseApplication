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





