new="4.0.3"
old="4.0.2"

find ../ -type f \( -name "pom.xml" -o -name "build.gradle*"  -o -name "README.md*" \) \
  -exec sed -i -e "s/<revision>$old<\/revision>/<revision>$new<\/revision>/g; s/escpos-coffee:$old/escpos-coffee:$new/g" {} \;
