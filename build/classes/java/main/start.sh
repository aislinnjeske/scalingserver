CLASSES="~/Documents/cs455/HW-2/HW-2/build/classes/java/main"
SCRIPT="cd $CLASSES; java cs455.scaling.client.Client atlanta 9000 10"
for ((j=1;j<=$1;j++));
do
    COMMAND='gnome-terminal'
    for i in `cat machine_list`
    do
        echo 'logging into '$i
        OPTION='--tab -e "ssh -t '$i' '$SCRIPT'"'
        COMMAND+=" $OPTION"
    done
    eval $COMMAND &
done
