# Base Application

Aplicativo base para criacao de apps usando react e jcef ja configurado.

para chamada de um servico utilize execService  do arquivo react/src/internal/serrano/serrano.ts;

Assinatura
execService(beanName:string,className:string,method:string,...args:any):Promise<any>


para registro de uma promise que possa ser chamada pelo java utilize
execService(beanName:string,className:string,method:string):Promise<any>



