script_root_path="$(dirname "$(readlink -f "$0")")"
output_folder_path=${script_root_path}/output

# date=$(date '+%d-%m-%Y_%H-%M')
# output_folder=${output_folder_path}/${date}
mkdir -p $output_folder_path

configurations=$(cat ${script_root_path}/configuration.list)

# read the all configuration file names from configuration.list
for config_args in $configurations
do
    edge_servers_file=$(echo $config_args | cut -d ';' -f1)
    micro_service_file=$(echo $config_args | cut -d ';' -f2)
    input_graph_file=$(echo $config_args | cut -d ';' -f3)
done

edge_servers_file_path=${script_root_path}/config/${edge_servers_file}
micro_service_file_path=${script_root_path}/config/${micro_service_file}
input_graph_file_path=${script_root_path}/config/${input_graph_file}

java -classpath '../bin' sample_app.MainApp $edge_servers_file_path $micro_service_file_path $input_graph_file_path $output_folder_path
python3 ../src/print_graph/input_graph/PrintInputGraph.py
python3 ../src/print_graph/output_graph/PrintOutputGraph.py