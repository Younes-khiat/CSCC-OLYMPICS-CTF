**Spyce is a medium rated challenge from CSCC-Olympics mini-ctf**
**The challenge features a website to book trips to Mars, The user can fill the form to get a ticket**
The source code is provided for this challenge

### Source code review:
viewing the source code
**File structure:**
```bash
└── app
    ├── app.py
    ├── DockerFile
    ├── __pycache__
    │   └── utils.cpython-310.pyc
    ├── templates
    │   └── index.html
    ├── tickets
    ├── uploads
    └── utils
        └── utils.py
```

First thing we notice is the `utils.py` is  precompiled 
**Code review:**
in `app-py`:
```python
@app.route('/app/<path:filename>')
def serve_source(filename):
    base = BASE_DIR
    target = os.path.abspath(os.path.join(base, filename))
    if not target.startswith(base):
        abort(403)
    if not os.path.exists(target):
        abort(404)
    return send_from_directory(os.path.dirname(target), os.path.basename(target))

```
This function allows reading files from the site base-dir

in `utils.py`:
```python
SECRET_MISSION_CODE = "CSCC{REDACTED}"
```

Reading the `utils.py` :
![[01.png]]

still redacted
checking the remaining files we get the `pyc` 
![[02.png]]

**Decompiling PYC files:**
we can use https://www.decompiler.com 

![[03.png]]

and we got the flag :`CSCC{r4nd0m_py_sh1t_38r88re888re88r838r838r8388}`
