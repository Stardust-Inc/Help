# Help
Easily document your plugins with the Help plugin and view it using the `/help` command

## Setting up
Inside your terminal do
```
git clone https://github.com/ATKatary/help.git
cp Help/target/Help-1.0-SNAPSHOT.jar <path_to_your_server>/plugins
```
Now inside of your minecraft server run 
```
/reload
```
Inside of your `<path_to_your_server>/plugins` should now see a folder named `Help` 
with another folder named `plugins` with a demo plugin inside called `demo.json`.
Inside of this folder you can add json files for each plugin you want to document. 
