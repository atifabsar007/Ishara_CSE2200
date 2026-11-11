"""Lightweight source sanity check; does NOT replace a full Android build."""
from pathlib import Path
from xml.etree import ElementTree as ET
import re
import subprocess
import tempfile
import shutil
ROOT = Path(__file__).resolve().parents[1]
res = ROOT / "app/src/main/res/layout"
errors=[]
xml_files=list((ROOT / 'app/src/main').rglob('*.xml'))
for p in xml_files:
    try: ET.parse(p)
    except ET.ParseError as exc: errors.append(f'Invalid XML in {p}: {exc}')
java_root=ROOT/'app/src/main/java/com/kuet/ishara/ui'
android_ns='{http://schemas.android.com/apk/res/android}'
for p in java_root.glob('*.java'):
    source=p.read_text(encoding='utf-8')
    binds=re.findall(r'\b(?:Activity|Item)[A-Z][A-Za-z]*Binding\b',source)
    for binding in set(binds):
        layout=re.sub('([a-z0-9])([A-Z])',r'\1_\2',binding.removesuffix('Binding')).lower()+'.xml'
        path=res/layout
        if not path.exists(): errors.append(f'Missing view binding layout {path}'); continue
        ids={n.attrib[android_ns+'id'][5:] for n in ET.parse(path).iter()
             if n.attrib.get(android_ns+'id','').startswith('@+id/')}
        # References may be `b.btnName` or `holder.b.btnName`. `getRoot()` is a generated method.
        # ViewBinding normalizes underscore-delimited resource IDs into camelCase.
        normalized_ids={parts[0]+''.join(x[:1].upper()+x[1:] for x in parts[1:]) for name in ids if (parts:=name.split('_'))}
        refs=set(re.findall(r'\b(?:b|binding)\.([A-Za-z]\w*)',source))
        for ref in refs - {'getRoot'}:
            if ref not in normalized_ids: errors.append(f'{p.name}: {binding}.{ref} absent in {layout}')

# Guard against the MaterialButton method typo found in Ishara 4.0.
for p in (ROOT/'app/src/main/java').rglob('*.java'):
    if 'setTextAllCaps(' in p.read_text(encoding='utf-8'):
        errors.append(f'Unsupported MaterialButton setter in {p}: use setAllCaps(boolean)')
# Compile only platform-independent classes: ModelInstaller uses Android Context/Log.
logic=[ROOT/'app/src/main/java/com/kuet/ishara/logic'/name for name in ('GestureDictionary.java','SentenceComposer.java','PhraseCatalog.java')]
if shutil.which('javac'):
    with tempfile.TemporaryDirectory() as out:
        cmd=subprocess.run(['javac','-encoding','UTF-8','-d',out,*map(str,logic)], capture_output=True,text=True)
        if cmd.returncode: errors.append(cmd.stderr)
if errors:
    for e in errors: print('ERROR:',e)
    raise SystemExit(1)
print(f'PASS: {len(xml_files)} XML files, view bindings and {len(logic)} independent Java logic classes.')
print('NOTE: This is static validation only. Run the Android Gradle build in Android Studio.')
