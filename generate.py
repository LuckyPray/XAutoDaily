# -*- coding: utf-8 -*-

import json
import os

src = {
    "app": "app-meta.json",
    "config": "conf-meta.json",
    "notice": "notice",
}

for key, value in src.items():
    data = None
    if os.path.exists(value):
        with open(value, 'r') as f:
            data = f.read()
            if len(data.strip()) == 0:
                data = None
    if data and value.endswith(".json"):
        data = json.loads(data)
    src[key] = data

with open('meta.json', 'w') as f:
    f.write(json.dumps(src, ensure_ascii=False, separators=(',', ':')))
