import os

def verificarStatus(valor, menssagem):
    if valor != 0 :
        raise  BaseException( menssagem)

verificarStatus( os.system("tsc"),"erro ao compilar tsc")


def corrigir_js(dir):
    for i in os.listdir(dir):
        caminho_completo = os.path.join(dir, i)
        if(os.path.isdir(caminho_completo)):
            corrigir_js(caminho_completo)
        else:
            with open(caminho_completo, 'r') as file:
                content = file.read()
            content = content.replace("export {};",'')
            content = content.replace("export",'')
            with open(caminho_completo, 'w') as file:
                file.write(content)


curr_dir = os.getcwd()
curr_dir = curr_dir  + "./../javaScript"
corrigir_js(curr_dir)



