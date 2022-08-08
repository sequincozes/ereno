<h1 align="center"> Bem-vindo ao ERENO-UI! </h1>
<h5 align="left"> Ferramenta com Interface Gráfica para 
A ferramenta possui diversas interfaces, ou módulos: </h5>

O ERENO-UI é uma ferramenta com interface gráfica que permite gerar datasets com mensagens dos protocolos GOOSE e SV para subestações elétricas. Dentre os módulos existentes, destacam-se quatro: o de configuração de parâmetros, definição de ataques, definição de corrente e tensão e de download do dataset final.


### Índice

* [Ambiente de Teste](#ambiente-de-teste)
* [Preparando o Ambiente](#preparando-o-ambiente)
* [Parâmetros Disponíveis](#parametros-disponiveis)
* [Exemplo de Uso](#exemplo-de-uso)

<div id="ambiente-de-teste"/>


### 🖱️ Ambiente de Teste - Execução Local

Executamos a ferramenta em dois computadores com configurações distintas:

|  Configurações   |  Computador I       |    Computador II |
| :---:        |     :---:      |    :---:      |
| Sistema Operacional (SO):   | Windows 10 | Ubuntu 20.04.3 LTS |
| Processador:  | Intel(R) Core(TM) <br> i5-10300H CPU @ 2.50GHz 2.50 GHz  | Intel(R) Core(TM) <br> i5-10300H CPU @ 2.50GHz 2.50 GHz |
| Memória RAM:   | 16 GB | 16 GB |
| bits do SO:  | 64 bits    | 86 bits |


Ambiente de desenvolvimento
```
IntelliJ IDEA 2022.2 (Ultimate Edition)
Runtime version: 17.0.3+7-b469.32 amd64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
```

Requisitos
A ferramenta usa dois utilitários para execução: Java SE Development Kit para executar arquivos java, e Apache Tomcat para executar um servidor web Java, mais especificamente um container de servlets. Na Tabela abaixo são apresentadas as versões dos utilitários utilizados.

|  Utilitários   |  Versão Testada     |
| :---:        |     :---:      |  
| Java SE Development Kit:   | 8 | 
| Apache Tomcat:  | 9.0.65 |

<div id="preparando-o-ambiente"/>

<!-- ### ⚙️Preparando o ambiente
Instalação do Git
```
sudo apt-get install git -y
```
Clone o Repositório
```
git clone https://github.com/sequincozes/ereno.git
```
Entre na pasta principal do projeto clonado e dê permissões para os arquivos.
```
cd ereno
``` -->

<!-- <div id="parametros-disponiveis"/>

### 📌 Parâmetros disponíveis:

```
max_time;
min_time;
etc..
``` 
-->

<div id="exemplo-de-uso"/>

### 👨‍💻 Exemplo de uso
```
Após clonar o projeto, configure um servidor Tomcat pelo IntelliJ (ou em ambiente de sua preferência). 
Execute o projeto com o servidor Tomcat (tecle Alt+Shift+F10 no IntelliJ)
Acesse a url: http://localhost:8080/ERENO_war/
As interfaces do ERENO-UI devem ser exibidas.
```
