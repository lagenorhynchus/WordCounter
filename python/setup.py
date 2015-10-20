import py2exe
from distutils.core import setup

option = {
    'compressed': 1,
    'optimize': 2,
    'bundle_files': 1
}

setup(
    options={
        'py2exe': option
    },
    console=[
        {'script': '../word_counter.py'}
    ],
    zipfile=None
)
