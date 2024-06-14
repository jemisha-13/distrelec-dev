# .bashrc

# Source global definitions
if [ -f /etc/bashrc ]; then
        . /etc/bashrc
fi

# User specific aliases and functions

########## changes from namics ##########
#export JAVA_HOME="/data/jdk1.7.0_17"
. /etc/java_home 
export ANT_HOME="/data/project_distrelec/hybrisserver/hybris/bin/platform/apache-ant-1.8.2"
export PATH="$JAVA_HOME/bin:$ANT_HOME/bin:$PATH" 

########## make bash look nice (rhaemmerli) ##########

export CLICOLOR=1
export LSCOLORS=GxFxCxDxBxegedabagaced
export TERM="xterm-color"
#export PS1="[\u@\h \W]\\$ "
#PS1='[\[\e[0;33m\]\u\[\e[0m\]@\[\e[0;32m\]\h\[\e[0m\]:\[\e[0;34m\]\w\[\e[0m\]]\$ '
#export PS1="[\u@\h \W]\\$ "

. /data/project_distrelec/install-release-env.rc
PS1='[\[\e[0;33m\]\u\[\e[0m\]@\[\e[0;32m\]\h\[\e[0m\]($install_environment $install_environment_node):\[\e[0;33m\]\w\[\e[0m\]]\$ '
export LC_CTYPE=en_US.utf8

# set terminal title
# echo -en "\033]2; $install_environment $install_environment_node \007"


########## changes by distrelec ##########
alias goto_projecthome='cd /data/project_distrelec'
alias goto_logs='cd /data/project_distrelec/hybrisserver/hybris/log'
alias goto_hotfolder='cd /data/project_distrelec/hybrisserver/hybris/data/acceleratorservices/import/distrelec/'
alias goto_factfinder='cd /data_nfs/env-hp-*/factfinder/ff-resources/FACT-Finder/'
alias goto_tomcat='cd /usr/share/tomcat7'
alias less='less -r'
alias show_logs='goto_logs;clear;tail -n30 -f console.log'

# set mask
umask 0002
