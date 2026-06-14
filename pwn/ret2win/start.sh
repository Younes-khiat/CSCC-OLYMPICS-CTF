#!/bin/sh
set -eu

cd /home/ctf
exec socat TCP-LISTEN:1337,reuseaddr,fork EXEC:"/home/ctf/challenge",stderr
