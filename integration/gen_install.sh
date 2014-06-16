#!/bin/sh

ISH=./install.sh
TOOCT=./bin_to_oct.sh
JARPATH=../testing/build/jar/Project_Icarus.jar

function write_install()
{
    cat >> $ISH <<EOF
#!/bin/sh

if [ id -u != 0]
then
    printf "You have to be root!\n"
    exit
fi

function pre_checks()
{
    
}

function info()
{
    printf
    "You are going to install Icarus
     the flying ST Interpreter!
     Are you really sure? [yes/no]"
     read answer
     if [ "$answer" != "yes" ]
     then
         printf "What? really \"$answer\"?"
         exit
     fi
     
}

function help()
{
    
}

EOF

    printf "printf '" >> $ISH
    sh $TOOCT $JARPATH >> $ISH
    printf "'" >> $ISH
}
