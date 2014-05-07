rest-fs
=====

restful interface to a filesystem

usage
=====

`npm start`
starts fileserver on port 3000

`npm test`
runs various file and folder test

`app = require('rest-fs')`
app is a express application which has all the file system routes defined
the app is not in the listening state

API
===


GET /path/to/dir/
-----------------
  list contents of directory
  
  *optional*<br>
  ?recursive = list recursively default false

  returns:
```
  [
    {
      "name" : "file1", // name of dir or file
      "path" : "/path/to/file", // path to dir or file 
      "dir" : false // true if directory
    },
    ...
  ]
```

GET /path/to/file
-----------------
  returns contents of file<br>
  if dir, redirect to dir path
  
  *optional*<br>
  ?encoding = default utf8

  returns:
  content of specified file as Content-Type "text/html"


POST /path/to/file/or/dir
-------------------------
  creates or overwrites file<br>
  creates dir if it does not exist.<br>
  renames or moves file if newPath exists<br>

  *optional*<br>
  body.newpath = if exist, move/rename file to this location.<br>
  body.clobber = if true will overwrite dest files (default false)<br>
  body.mkdirp = if true will create path to new location (default false)<br>
  body.mode = permissions of file (defaults: file 438(0666) dir 511(0777))<br>
  body.encoding = default utf8

  returns: nothing

PUT /path/to/file
-----------------  
  creates file

  *optional*<br>
  body.mode = permissions of file (438 default 0666 octal)<br>
  body.encoding = default utf8

  returns: nothing

DEL /path/to/dir/
-----------------  
  deletes folder<br>
  if file returns error

  returns: nothing

DEL /path/to/file
-----------------  
  deletes file<br>
  if folder returns error

  returns: nothing