<h1 align="center"> 📌 Bem-vindo ao ERENO-UI! 📌 </h1>
<h4 align="left"> ✔️ O ERENO-UI é uma ferramenta com interface gráfica que permite a geração de datasets com mensagens dos protocolos GOOSE e SV para subestações elétricas. Dentre os módulos existentes, destacam-se quatro: o de configuração de parâmetros, definição de ataques, definição de corrente e tensão e, por fim, o de download do dataset final. </h4>



### Índice

* [Ambiente de Teste](#ambiente-teste)
* [Requisitos](#requisitos)
* [Ambiente de Desenvolvimento](#ambiente-desenvolvimento)
* [Exemplo de Uso](#exemplo-de-uso)

<div id="ambiente-de-teste"/>

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
