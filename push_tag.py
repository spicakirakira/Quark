import os
from jproperties import Properties

def main():
	build = Properties()
	with open('build.properties', 'rb') as f:
	    build.load(f , "utf-8")

	mc_version, mcv_meta = build['mc_version']
	version, v_meta = build['version']
	build_number, bn_meta = build['build_number']

	print('MC Version:', mc_version)
	print('Version:', version)
	print('Build Number', build_number)

	changelog = ''
	with open('changelog.txt', 'r') as f:
		changelog = f.read()

	build['build_number'] = str(int(build_number) + 1)
	with open("build.properties", "wb") as f:
	    build.store(f, encoding="utf-8")

	os.system('git tag -a release-{}-{}-{} -m "{}"'.format(mc_version, version, build_number, changelog))
	os.system('git commit -a -m build')
	os.system('git push origin master release-{}-{}-{}'.format(mc_version, version, build_number))

if __name__ == '__main__':
	main()