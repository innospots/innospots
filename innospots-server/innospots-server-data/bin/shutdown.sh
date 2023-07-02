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


SERVER_MAIN_CLASS="io.innospots.data.server.InnospotDataServer"
APP_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd | sed 's/\/bin//')
PID_FILE=${APP_DIR}/proc_pid

TIMES=0
TIMEOUT=20
TPID=0

getPid(){
  TPID=$(pgrep -f "${SERVER_MAIN_CLASS}")
}

shutdown(){
    getPid
    if [[ ${TPID} -ne 0 ]]; then
        if [[ $1 -ne 0 ]]; then
            echo "=========================Stopping Service=============================="
            kill "${TPID}"

            if [[ $? -eq 0 ]]; then
                echo ""
            else
                echo "[Service Stop Failed]"
                return
            fi

            echo -n "Stopping ${SERVER_MAIN_CLASS} (PID=${TPID})...";
        fi


        while [[ ${TPID} -ne 0 ]] && [[ ${TIMES} -lt ${TIMEOUT} ]]
        do
            getPid
            ((TIMES++))
            sleep 1
            echo -n "."
        done

    else
        echo "${SERVER_MAIN_CLASS} is not running"
        return
    fi

    sleep 1
    echo "."
    getPid
    if [[ ${TPID} -eq 0 ]]
    then
        if [[ -e ${PID_FILE} ]]; then
            rm -f "${PID_FILE}"
        fi
        echo "Service Stop Success."
    elif [[ ${TIMES} -gt ${TIMEOUT} ]] || [[ ${TIMES} -eq ${TIMEOUT} ]]
    then
        echo "Service Stop Time out, please kill -9 ${TPID}"
    fi

}

shutdown 3 "$1"