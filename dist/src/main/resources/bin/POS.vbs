Set WshShell = CreateObject("WScript.Shell") 
Set fso = CreateObject("Scripting.FileSystemObject")
GetTheParent = fso.GetParentFolderName(WScript.ScriptFullName)
WshShell.Run chr(34) & GetTheParent & "\POS.bat" & Chr(34), 0
Set WshShell = Nothing