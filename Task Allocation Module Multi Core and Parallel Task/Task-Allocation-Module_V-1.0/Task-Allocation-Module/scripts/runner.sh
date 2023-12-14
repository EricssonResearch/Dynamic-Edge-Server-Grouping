script_root_path="$(dirname "$(readlink -f "$0")")"

configurations=$(cat ${script_root_path}/configuration.list)

# read all the configuration file names from configuration.list
for config_args in $configurations
do
    edge_servers_file=$(echo $config_args | cut -d ';' -f1)
    application_file=$(echo $config_args | cut -d ';' -f2)
done

edge_servers_file_path=${script_root_path}/config/${edge_servers_file}
application_file_path=${script_root_path}/config/${application_file}

java -classpath '../bin' sample_app.MainApp $edge_servers_file_path $application_file_path