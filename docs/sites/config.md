# Configuration

[<kbd>&larr; Go Back</kbd>](../../README.md)

### Adding controlsfx to the project

- Navigate to the "Run" menu at the top of IntelliJ IDEA.

- Select "Edit Configurations..."

- In the "Run/Debug Configurations" dialog that appears, find your Java application configuration on the left side
  under "Application."

- Under the "Configuration" tab, locate the "VM options" field.
    - if there is nothing, click on "Modify options" - there should "Add VM options" appear

- Enter the VM option provided by ControlsFX into the "VM options" field. In your case, it would be:

```
--add-exports javafx.base/com.sun.javafx.event=org.controlsfx.controls
```

### Creating an .exe-file

- create a .jar-file with:

```
mvn clean package
```

- download Launch4j: https://launch4j.sourceforge.net
    - under downloads (windows, linux,...)
- start Launch4j and open this file: [<kbd>configuration</kbd>](../../config/launch_config.xml)
- Specify:
    - appropriate jar-file (from target folder)
    - output file (don't forget .exe)
    - an icon (not png or jpg format)
    - under JRE:
        - the path to JRE
        - JVM options:

```
--module-path "C:\Program Files\Java\javafx-sdk-23\lib"
--add-modules javafx.controls,javafx.base
```

- and set a splash-file (picture that is shown, while the application starts)