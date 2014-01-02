PRINC := "${@int(PRINC) + 1}"

# glew is a depend (see http://patchwork.openembedded.org/patch/55603/)
DEPENDS += " glew "
