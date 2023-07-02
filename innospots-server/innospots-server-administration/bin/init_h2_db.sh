#!/usr/bin/env bash 
 
# Licensed under the Apache License, Version 2.0 (the "License"); 
# you may not use this file except in compliance with the License. 
# You may obtain a copy of the License at 
 
#      http://www.apache.org/licenses/LICENSE-2.0 
# 
# Unless required by applicable law or agreed to in writing, software 
# distributed under the License is distributed on an "AS IS" BASIS, 
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
# See the License for the specific language governing permissions and 
# limitations under the License.

APP_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd | sed 's/\/bin//')
DB_DIR=${APP_DIR}/h2_db
echo "h2 db directory: ${DB_DIR}"

read -p "Do you want to initialize h2 db? all data will be deleted and the operation is irreversible. Yy|Nn (default=N): " para

case $para in 
	[yY])
		echo "Start initialize h2 db......"
    rm -rf ${DB_DIR}/*
    cp ${APP_DIR}/docs/init_h2_db/*  ${DB_DIR}/
    echo "Successfully initialize h2 db......"
		;;
	[nN])
		echo "${para}"
    echo "Initialize operation has been canceled......"
    exit 1
		;;
	*)
		echo "Initialize operation has been canceled......"
		exit 1
esac # end case
