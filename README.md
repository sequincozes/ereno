# 📌 ERENO v2 📌

*🇵🇹/🇧🇷 ERENO v2 é uma versão simplificada do [ERENO original](https://github.com/sequincozes/ereno), focada na geração de datasets com mensagens dos protocolos GOOSE e SV para subestações elétricas.*\
*🇺🇸 ERENO v2 is a simplified version of the [original ERENO](https://github.com/sequincozes/ereno), focused on generating datasets with messages from the GOOSE and SV protocols for electrical substations.*\
*🇪🇸 ERENO v2 es una versión simplificada del [ERENO original](https://github.com/sequincozes/ereno), centrada en la generación de conjuntos de datos con mensajes de protocolo GOOSE y SV para subestaciones eléctricas.*
---

<details>
<summary><strong>🇧🇷 Português (Brasil)</strong></summary>

---

ERENO v2 é uma ferramenta para geração de datasets com mensagens dos protocolos GOOSE e SV para subestações elétricas.

### ⚡ O que mudou?

- **Não é mais necessário baixar arquivos de medidas elétricas** — já estão embedados no projeto.
- **Não existe mais interface web nem Apache Tomcat** — a ferramenta roda como aplicação Java.
- **Uso recomendado com IntelliJ IDEA** — carrega as configurações de execução automaticamente.
- **Gerar datasets é simples:**
    1. Configure o arquivo `params.properties` com os parâmetros desejados (se não configurar, valores default serão usados).
    2. Rode a classe do cenário desejado (se não criada, o padrão é a classe `SamambaiaCenario`).

### 🚀 Como gerar datasets

1. Abra o projeto no IntelliJ IDEA (recomendado).
2. Configure os parâmetros no arquivo `params.properties`.
3. Escolha ou crie sua classe de cenário:
    - Para configurar qual ataque será gerado, defina o IED atacante na classe do cenário, conforme documentação embedada.
4. Execute a classe de cenário.
5. O dataset será gerado automaticamente.

### 🗂️ Visão geral do projeto

- Os arquivos principais do projeto estão comentados, explicando suas funções.
- As classes de cenário definem a geração dos datasets e os ataques aplicados.
- O arquivo `params.properties` controla os parâmetros chave da geração.

### 📚 Precisa de ajuda?

Veja os comentários dentro dos arquivos principais e das classes de cenário — eles explicam como customizar o projeto.

</details>

<details>
<summary><strong>🇺🇸 English (US)</strong></summary>

---

ERENO v2 is a simplified version of the original [ERENO](https://github.com/sequincozes/ereno), focused on generating datasets with GOOSE and SV protocol messages for electrical substations.

### ⚡ What’s new?

- **No need to download electrical measures files anymore** — they are embedded in the project.
- **No web interface or Apache Tomcat needed** — the tool runs as a Java application.
- **Recommended to use IntelliJ IDEA** — it loads run configurations automatically.
- **Dataset generation is simple:**
    1. Configure `params.properties` with desired parameters (if not configured, defaults are used).
    2. Run the desired scenario class (if none created, the default is `SamambaiaCenario`).

### 🚀 How to generate datasets

1. Open the project in IntelliJ IDEA (recommended).
2. Configure your parameters in the `params.properties` file.
3. Choose or create your scenario class:
    - To set which attack to simulate, configure the attacking IED inside the scenario class, as described in the embedded documentation.
4. Run the scenario class.
5. Your dataset will be generated automatically.

### 🗂️ Project overview

- Main project files are well-commented to explain their role.
- Scenario classes define how datasets are generated and which attacks are applied.
- `params.properties` controls key parameters for generation.

### 📚 Need help?

Check the comments inside the main files and scenario classes — they explain everything you need to customize.

</details>

<details>
<summary><strong>🇪🇸 Español</strong></summary>

---

ERENO v2 es una versión simplificada del [ERENO original](https://github.com/sequincozes/ereno), centrada en la generación de datasets con mensajes de los protocolos GOOSE y SV para subestaciones eléctricas.

### ⚡ ¿Qué hay de nuevo?

- **No es necesario descargar archivos de medidas eléctricas** — ya están incorporados en el proyecto.
- **No existe interfaz web ni Apache Tomcat** — la herramienta funciona como una aplicación Java.
- **Se recomienda usar IntelliJ IDEA** — carga las configuraciones de ejecución automáticamente.
- **Generar datasets es sencillo:**
    1. Configure el archivo `params.properties` con los parámetros deseados (si no se configuran, se usan valores por defecto).
    2. Ejecute la clase de escenario deseada (si no se crea, la clase por defecto es `SamambaiaCenario`).

### 🚀 Cómo generar datasets

1. Abra el proyecto en IntelliJ IDEA (recomendado).
2. Configure los parámetros en el archivo `params.properties`.
3. Elija o cree su clase de escenario:
    - Para configurar qué ataque se generará, defina el IED atacante dentro de la clase de escenario, según la documentación incorporada.
4. Ejecute la clase de escenario.
5. El dataset se generará automáticamente.

### 🗂️ Visión general del proyecto

- Los archivos principales del proyecto están comentados, explicando sus funciones.
- Las clases de escenario definen cómo se generan los datasets y qué ataques se aplican.
- El archivo `params.properties` controla los parámetros clave para la generación.

### 📚 ¿Necesita ayuda?

Revise los comentarios dentro de los archivos principales y las clases de escenario — explican cómo personalizar el proyecto.

</details>
