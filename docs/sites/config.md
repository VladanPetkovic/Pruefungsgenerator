# Configuration

[<kbd>&larr; Go Back</kbd>](../../README.md)

### Adding controlsfx to the project

- Navigate to the "Run" menu at the top of IntelliJ IDEA.

- Select "Edit Configurations..."

- In the "Run/Debug Configurations" dialog that appears, find your Java application configuration on the left side under "Application."

- Under the "Configuration" tab, locate the "VM options" field.

- Enter the VM option provided by ControlsFX into the "VM options" field. In your case, it would be:
```
--add-exports javafx.base/com.sun.javafx.event=org.controlsfx.controls
```