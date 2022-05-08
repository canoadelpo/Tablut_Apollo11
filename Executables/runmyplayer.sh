#!/bin/bash

if [[ $# -ne 3 ]] ; then
  echo "Servono tutti gli argomenti"
  echo "Inserire WHITE o BLACK come primo parametro"
  echo "Il timeout in secondi come secondo parametro"
  echo "L'ip del server come terzo parametro"
  echo "Ad esempio: $0 WHITE 60 192.168.20.254"
  exit 1
fi

java -jar /home/tablut/Tablut_Apollo11/apollo11.jar "$1" "$2" "$3"
