# WebLinkedServer
Host your files on multiple servers while providing users a single entry point into the network along with combined directory listing.

## In Detail ##
**So what does this really do man? You're a little unclear in your explanation above.**
WebLinkedServer allows you to distribute your files across multiple servers (useful if your disk space on each server is very low) and serve them all from one server.
Give WebLinkedServer a list of servers and it will redirect any file requests to it to the server where the file resides on.
It also provides a combined directory listing of all the files on all the servers.

## Server Priorities ##
**What happens if I have the same file on two (or more) servers?**
WebLinkedServer will serve the file on the server nearest to the top of the server list it is configured with.

## Combined Directory Listing ##
**What if the user lists a directory that is present on two (or more) servers?**
WebLinkedServer will combine all the files/folders in each directory into one listing. The stats of each file are retrieved from the server with the highest priority as mentioned above.

## Server Roles ##
### Slave ###
A regular HTTP server except for one thing. It will return a JSON list of files in the directory specified when queried at `/_WSL/LIST_FILES/{directory}`
All other requests are served just like in a regular HTTP server.

### Master ###
When a request is made to the master server, it first checks each of it's slave servers for the path specified in the query.
If the path specified is a directory (on the highest priority server), it will list it as a directory and combine all files in each of the corresponding directories in each of the slave servers into one page.
If the path specified is a file (on the highest priority server), it will redirect the request to the highest priority server that has the file.
If the path specified does not exist, it will just 404.

## Configuration ##
Configuration is stored in the working directory in the file **'WebLinkedServer.config'** in the Java properties style.
Here are all the possible configuration entries and their description:
```
server.role = The role of the server (MASTER or SLAVE). Defaults to: SLAVE
server.port = The port which the server will bind to. Defaults to: 1919
server.ip = The IP which the server will bind to. Defaults to: 0.0.0.0
master.serverListFile = The location of the server list. Defaults to: WLSServers.json in the working directory
master.listDirectories = Whether or not the master server will list directories (the slaves still will). Defaults to: true
slave.rootDir = The location of the directory from where the files will be served from. Defaults to the working directory
slave.useSlowTypeDetection = Whether or not to use slow file type detection (true/false). Will also increase memory usage. Defaults to: false
```

## Server List File ##
The server list is stored in the file specified by: `master.serverListFile`
It is in JSON format, here is an example:
```
{
  "servers": [
    "http://localhost:1918",
    "http://localhost:1917",
    {
      "internal": "http://127.0.0.1:1916",
      "external": "https://example.com:1916"
    }
  ]
}
```

## Credits ##
The theme for the directory listing comes from Joni's Simple Index: http://jomppa.net/projects/simple-index/