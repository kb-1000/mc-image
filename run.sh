#!/usr/bin/env bash
#
# Copyright (c) 2021-2022 kb1000.
#
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.
#

LD_LIBRARY_PATH=$(realpath natives)
export LD_LIBRARY_PATH

if TEMP=$(getopt -l 'no-optimize' -o 'g' -- "$@"); then
    true
else
    echo 'Terminating...' >&2
    exit 1
fi

eval set -- "$TEMP"
unset TEMP
wrapper=()
optimize=true

while true; do
    case "$1" in
        '-g')
            wrapper=(gdb --args)
            shift
            continue
        ;;
        '--no-optimize')
            optimize=false
            shift
            continue
        ;;
        '--')
            shift
            break
        ;;
        *)
            echo 'Internal error!' >&2
            exit 1
        ;;
    esac
done


exec "${wrapper[@]}" ./minecraft-client$(if ! $optimize; then echo -n -unoptimized; fi) "$@" --version 1.17.1 --accessToken "" --assetIndex 1.17
