
### ENLISH VERSION 🇺🇸
<div id="english-version"/>

<h1 align="center"> 📌  Welcome to ERENO-UI! 📌 </h1>

<h4 align="left"> ✔️ERENO-UI is a tool with a graphical interface that allows the generation of datasets with GOOSE and SV protocol messages for electrical substations. Among the existing modules, four stand out: parameter configuration, attack definition, current and voltage definition and, finally, the final dataset download. </h4>

### Index

* [Test Environment](#test-environment)
* [Requirements](#requirements)
* [Development Environment](#development-environment)
* [Example of Usage](#example-of-use)
* [🇧🇷 Versão em português!](#portuguese-version)


   


### **🏷️ Note**
In this implementation, we use the Java SE Development Kit 8 to run java files, Apache Tomcat to run a Java web server (more specifically a servlet container) and the IDE IntelliJ IDEA 2022.2 (Ultimate Edition). Next, the execution environments are detailed and the versions of the utilities are presented.


   
<div id="test-environment"/>
### 🖱️ Test Environments

For local testing purposes, we ran the tool on two computers with different configurations:

|  Settings   |  Computer I       |    Computer II |
| :---:        |     :---:      |    :---:      |
|  Operating System (OS):   | Windows 10 | Ubuntu 20.04.3 LTS |
| Processor:  | Intel(R) Core(TM) <br> i5-10300H CPU @ 2.50GHz 2.50 GHz  | Intel(R) Core(TM) <br> i5-10300H CPU @ 2.50GHz 2.50 GHz |
| RAM Memory:   | 16 GB | 16 GB |
| Architecture:  | 64 bits    | 86 bits |


   

<div id="requirements"/>
## 📝 Requirements

The tool uses two utilities to run. The table below shows the versions of the utilities used.


|  Utilities   |  Tested Version     |
| :---:        |     :---:      |  
| Java SE Development Kit:   | 8 | 
| Apache Tomcat:  | 9.0.65 |


   

<div id="development-environment"/>

## ⚙️ Integrated Development Environment (IDE)

|  Tool  |   Tested Version     | Runtime Version | VM |
| :---:        |     :---:      |  :---:      |  :---:      |
| IntelliJ IDEA   | 2022.2 (Ultimate Edition) | 17.0.3+7-b469.32 amd64 | OpenJDK 64-Bit Server VM by JetBrains s.r.o. |





   

<div id="example-of-use"/>

### 👨‍💻 Usage example with IntelliJ
Next, we describe the necessary steps to run the tool in IntelliJ. It is worth mentioning that you can use the development environment you prefer, as long as it supports the Java Web.

```
1. Clone the ERENO-UI project by entering the url: https://github.com/sequincozes/ereno.git
2. Set up a Tomcat server in IntelliJ.
3. Run the project in IntelliJ (Alt+Shift+F10). Automatically the Tomcat server will run.
4. Access the url: http://localhost:8080/ERENO_war/
5. ERENO-UI interfaces should be displayed.
```

### VERSÃO EM PORTUGUÊS 🇧🇷
<div id="portuguese-version"/>

<h1 align="center"> 📌 Bem-vindo ao ERENO-UI! 📌 </h1>

<h4 align="left"> ✔️ O ERENO-UI é uma ferramenta com interface gráfica que permite a geração de datasets com mensagens dos protocolos GOOSE e SV para subestações elétricas. Dentre os módulos existentes, destacam-se quatro: o de configuração de parâmetros, definição de ataques, definição de corrente e tensão e, por fim, o de download do dataset final. </h4>



### Índice
* [Ambiente de Teste](#ambiente-teste)
* [Requisitos](#requisitos)
* [Ambiente de Desenvolvimento](#ambiente-desenvolvimento)
* [Exemplo de Uso](#exemplo-de-uso)
* [🇺🇸 English version!](#english-version)

<div id="nota"/>

### **🏷️ Nota** 
Nesta implementação, utilizamos o Java SE Development Kit 8 para executar arquivos java, o Apache Tomcat para executar um servidor web Java (mais especificamente um container de servlets) e a IDE IntelliJ IDEA 2022.2 (Ultimate Edition). Na sequência os ambientes de execução são detalhados e as versões dos utilitários são apresentadas.

<div id="ambiente-teste"/>

### 🖱️ Ambientes de Testes

Para fins de testes locais, executamos a ferramenta em dois computadores com configurações distintas:

|  Configurações   |  Computador I       |    Computador II |
| :---:        |     :---:      |    :---:      |
| Sistema Operacional (SO):   | Windows 10 | Ubuntu 20.04.3 LTS |
| Processador:  | Intel(R) Core(TM) <br> i5-10300H CPU @ 2.50GHz 2.50 GHz  | Intel(R) Core(TM) <br> i5-10300H CPU @ 2.50GHz 2.50 GHz |
| Memória RAM:   | 16 GB | 16 GB |
| bits do SO:  | 64 bits    | 86 bits |

<div id="requisitos"/>

## 📝 Requisitos

A ferramenta usa dois utilitários para execução. Na Tabela abaixo são apresentadas as versões dos utilitários utilizados.

|  Utilitários   |  Versão Testada     |
| :---:        |     :---:      |  
| Java SE Development Kit:   | 8 | 
| Apache Tomcat:  | 9.0.65 |


<div id="ambiente-desenvolvimento"/>

## ⚙️ Ambiente de Desenvolvimento Integrado

|  Ferramenta  |  Versão Testada     | Versão Runtime | VM |
| :---:        |     :---:      |  :---:      |  :---:      |
| IntelliJ IDEA   | 2022.2 (Ultimate Edition) | 17.0.3+7-b469.32 amd64 | OpenJDK 64-Bit Server VM by JetBrains s.r.o. |



<div id="exemplo-de-uso"/>

### 👨‍💻 Exemplo de uso com IntelliJ
A seguir, descrevemos os passos necessários para executar a ferramenta no IntelliJ. Vale ressaltar que você pode usar o ambiente de desenvolvimento que preferir, desde que o mesmo dê suporte à Java Web. 

```
1. Clone o projeto ERENO-UI inserindo a url: https://github.com/sequincozes/ereno.git
2. Configure um servidor Tomcat no IntelliJ. 
3. Execute o projeto no IntelliJ (Alt+Shift+F10). Automaticamente o servidor Tomcat será executado.
4. Acesse a url: http://localhost:8080/ERENO_war/
5. As interfaces do ERENO-UI devem ser exibidas.
```
