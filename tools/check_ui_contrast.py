"""Static Ishara 6.0.1 contrast regression tests; no Android SDK needed.
This does NOT substitute for screenshots and accessibility testing on real Android phones.
"""
from pathlib import Path
import xml.etree.ElementTree as ET
import re
root=Path(__file__).resolve().parents[1]
layout=root/'app/src/main/res/layout'
res=root/'app/src/main/res'
java=root/'app/src/main/java/com/kuet/ishara/ui'
a='{http://schemas.android.com/apk/res/android}'
b='{http://schemas.android.com/apk/res-auto}'
errors=[]
for name in ('themes.xml','colors.xml'):
 ET.parse(res/'values'/name)
theme=(res/'values/themes.xml').read_text()
if 'Theme.MaterialComponents.Light.NoActionBar' not in theme or 'parent="Theme.MaterialComponents.DayNight' in theme:errors.append('Theme is not a consistent accessible light theme')
if '<item name="android:forceDarkAllowed">false</item>' not in theme:errors.append('Forced dark prevention absent')
for file in layout.glob('*.xml'):
 for elem in ET.parse(file).iter():
  tag=elem.tag.split('}')[-1].split('.')[-1]
  if tag in ('EditText','TextInputEditText'):
   for attr in ('textColor','textColorHint'):
    if elem.get(a+attr) not in ('@color/ink','@color/muted'):errors.append(f'{file.name}: missing readable {attr} on {tag}')
  if tag=='TextInputLayout':
   for attr in ('boxBackgroundColor','boxStrokeColor','hintTextColor','helperTextTextColor'):
    if not elem.get(b+attr):errors.append(f'{file.name}: missing {attr} on TextInputLayout')
   if not elem.get(a+'textColorHint'):errors.append(f'{file.name}: missing TextInputLayout default hint color')
  if tag=='TextView' and elem.get(a+'hint') is not None and not elem.get(a+'textColorHint'):
   errors.append(f'{file.name}: missing visible TextView hint color')
for name in ('GestureActivity','CaptionActivity','ConversationActivity','CalibrationActivity','SimulatorActivity'):
 if 'simple_spinner_dropdown_item' in (java/(name+'.java')).read_text():errors.append(name+' uses system-color spinner')
 if 'new IsharaSpinnerAdapter(' not in (java/(name+'.java')).read_text():errors.append(name+' not using contrast-safe adapter')
if not (layout/'ishara_spinner_item.xml').exists():errors.append('Spinner item layout absent')
if not (layout/'ishara_spinner_dropdown_item.xml').exists():errors.append('Spinner dropdown layout absent')

def lum(h):
 cc=[int(h[i:i+2],16)/255 for i in (1,3,5)]
 cc=[u/12.92 if u<=0.04045 else ((u+.055)/1.055)**2.4 for u in cc]
 return .2126*cc[0]+.7152*cc[1]+.0722*cc[2]
def contrast(x,y):
 l1,l2=sorted((lum(x),lum(y)),reverse=True)
 return (l1+.05)/(l2+.05)
colors={elem.get('name'):(elem.text or '').strip() for elem in ET.parse(res/'values/colors.xml').iter('color')}
for foreground, background in [('ink','white'),('ink','primarySoft'),('muted','white'),('muted','primarySoft'),('white','primary')]:
 r=contrast(colors[foreground],colors[background]);print(f'Contrast {foreground} on {background}: {r:.2f}:1')
 if r<4.5:errors.append(f'Insufficient contrast {foreground} on {background}: {r:.2f}')
if errors:
 print('\n'.join('FAILED: '+e for e in errors));raise SystemExit(1)
print(f'PASS: {len(list(layout.glob("*.xml")))} layout files and all input, spinner and core contrast checks')
