#!/usr/bin/env zsh

#set -x

if [[ `ps | grep $$ | awk '{ print $4 }'` != zsh ]]
then
    echo "Error: zsh needed."
    echo "Error: Please use \"zsh java-switcher.sh\" or \"./java-switcher.sh\""
    exit 1
fi

setVars()
{
    JAVA=/usr/bin/java
    JAVAC=/usr/bin/javac
    
    case `hostname` in
	beelzebub)
	    ORACLE=/usr/lib/jvm/jdk1.7.0_51/bin
	    OPEN=/usr/lib/jvm/java-7-openjdk/bin
	    IBM=/usr/lib/jvm/ibm-java-x86_64-71/bin
	    ;;
	d4ryus)
	    ORACLE=/home/d4ryus/coding/gay_java/jdk1.8.0_05/bin
	    OPEN=/usr/lib/jvm/java-7-openjdk/bin
	    IBM=/path/to/java
	    ;;
	*)
	    print "... "
	    sleep 1
	    print "Idiot!"
	    ;;
    esac
}

setJDK()
{
    if [[ ! -d "${1}" ]]
    then
	print "No $2 JDK at ${1}"
	exit 1
    fi
    print "Activate $2 JDK"
    print -n "Root "
    su - -c zsh -c "\
    rm $JAVA;\
    rm $JAVAC;\
    ln -s $1/java $JAVA;\
    ln -s $1/javac $JAVAC;"
    if [[ $? != 0 ]]
    then
	print "Something went wrong ..."
    else
	print "Succes!"
    fi
}

help()
{
    print "Usage: java-switcher.sh [NAME]"
    print " "
    print "Names:"
    print "\toracle\t\t-\tOralce JDK"
    print "\topen\t\t-\tOpen JDK"
    print "\tibm\t\t-\tIBM JDK"
    print " "
    print "Don't forget to add your machine to the setVars function."
    print "Have fun ;)"
}

# script start

setVars

case $1 in
    Oracle|oracle)
	setJDK $ORACLE Oracle
	;;
    Open|open)
	setJDK $OPEN Open
	;;
    IBM|Ibm|ibm)
	setJDK $IBM IBM
	;;
    *)
	help
	;;
esac

