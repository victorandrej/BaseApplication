import os
import shutil
import xml.etree.ElementTree as ET

def remove_namespace(doc):
    for elem in doc.iter():
        if '}' in elem.tag:
            elem.tag = elem.tag.split('}', 1)[1]  # Remove the namespace part
    return doc

def verificarStatus(valor, menssagem):
    if valor != 0 :
        raise  BaseException( menssagem)
    



    

folder_name = 'distribuicao'
view_folder_name = 'view';
initial_dir = curr_dir = os.getcwd()
react_path = initial_dir + '/react/'
dist_folder = initial_dir+"/"+folder_name+"/"
view_folder = dist_folder +'view/'
react_dist_path = react_path+"dist/"
java_dir = initial_dir+"/java/"
java_target_dir = java_dir + "target/"

if(os.path.exists(dist_folder)):
    shutil.rmtree(dist_folder)
os.makedirs(view_folder)

os.chdir(react_path)

print(react_path)
verificarStatus( os.system("npm run build"),'erro ao compilar angular')


files = os.listdir(react_dist_path)
for file_name in files:
    shutil.move(os.path.join(react_dist_path,file_name), view_folder)


curr_dir =   initial_dir + '/./java/src/main/resources/typeScript'

os.chdir(curr_dir)
 

verificarStatus(os.system("py .\\Compile.py"),"erro ao compilar typescript");



curr_dir = java_dir
os.chdir(curr_dir)


try:
    verificarStatus(os.system("mvn clean package"),'Erro ao compilar java')
    



    files = os.listdir(java_target_dir)

    for file_name in files:
        if file_name.lower().endswith('.jar') or  (os.path.isdir(os.path.join(java_target_dir,file_name)) and file_name.lower() == 'lib') :
            shutil.move(os.path.join(java_target_dir, file_name), dist_folder)
finally:
    pass
    

