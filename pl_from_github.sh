#! /bin/bash

reps="origin"
branch=$(git symbolic-ref HEAD | sed -e 's,.*/\(.*\),\1,')

if [ $# == 1 ];then
    reps=$1
elif [ $# == 2 ];then
    reps=$1
    branch=$2
fi
echo "===========================\n"
printf "\e[1;32m---> pull code from ${reps} ${branch} \e[0m\n"
echo "===========================\n"

git pull $reps $branch 
