#!/usr/bin/env bash

SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )"≠ && pwd )

CMD="java -Xms2G -Xmx2G -cp "${SCRIPT_DIR}/lib/*" scratch.memory.args.MemoryArgs -Xms1G -Xmx1G"
echo "$CMD"
${CMD}

CMD="java -Xms1G -Xmx1G -cp "${SCRIPT_DIR}/lib/*" scratch.memory.args.MemoryArgs -Xms2G -Xmx2G"
echo "$CMD"
${CMD}

CMD="java -Xms2G -Xmx2G -cp "${SCRIPT_DIR}/lib/*" -Xms1G -Xmx1G scratch.memory.args.MemoryArgs"
echo "$CMD"
${CMD}

CMD="java -Xms1G -Xmx1G -cp "${SCRIPT_DIR}/lib/*" -Xms2G -Xmx2G scratch.memory.args.MemoryArgs"
echo "$CMD"
${CMD}
