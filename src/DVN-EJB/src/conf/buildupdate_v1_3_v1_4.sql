UPDATE pagedef SET path=regexp_replace(pagedef.path, '.jsp', '.xhtml', 'g')

