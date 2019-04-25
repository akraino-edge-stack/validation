#!/bin/bash

# Check that the last commit message contains the "Signed-off-by <>" line

if [ -z "$(git log -1 --show-signature |grep Signed-off-by)" ]; then
echo "Signed-off-by is missing from the commit message; please run \"git commit -s\""
exit 1
fi
