#!/usr/bin/env bash
set -euo pipefail

DOCS_DIR="docs"
OUTPUT_DIR="docs/html"

mkdir -p "$OUTPUT_DIR"

if ! command -v pandoc &> /dev/null; then
  echo "Installing pandoc..."
  sudo apt-get update -qq && sudo apt-get install -y -qq pandoc
fi

CSS_FILE="$OUTPUT_DIR/style.css"
cat > "$CSS_FILE" << 'CSSEOF'
:root {
  --bg: #0d1117;
  --surface: #161b22;
  --border: #30363d;
  --text: #c9d1d9;
  --text-muted: #8b949e;
  --accent: #58a6ff;
  --green: #3fb950;
  --yellow: #d29922;
  --red: #f85149;
}
* { box-sizing: border-box; margin: 0; padding: 0; }
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Helvetica, Arial, sans-serif;
  background: var(--bg);
  color: var(--text);
  line-height: 1.6;
  max-width: 960px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
}
h1 { font-size: 2rem; margin: 2rem 0 1rem; border-bottom: 1px solid var(--border); padding-bottom: .5rem; }
h2 { font-size: 1.5rem; margin: 2rem 0 .75rem; color: var(--accent); }
h3 { font-size: 1.2rem; margin: 1.5rem 0 .5rem; }
h4 { font-size: 1rem; margin: 1rem 0 .5rem; color: var(--text-muted); }
p { margin: .75rem 0; }
a { color: var(--accent); text-decoration: none; }
a:hover { text-decoration: underline; }
table { width: 100%; border-collapse: collapse; margin: 1rem 0; font-size: .9rem; }
th { background: var(--surface); color: var(--accent); text-align: left; padding: .5rem .75rem; border: 1px solid var(--border); }
td { padding: .5rem .75rem; border: 1px solid var(--border); }
tr:nth-child(even) { background: rgba(22,27,34,.5); }
code { background: var(--surface); padding: .15rem .4rem; border-radius: 4px; font-size: .85rem; }
pre { background: var(--surface); border: 1px solid var(--border); border-radius: 6px; padding: 1rem; overflow-x: auto; margin: 1rem 0; }
pre code { background: none; padding: 0; }
blockquote { border-left: 3px solid var(--accent); padding: .5rem 1rem; margin: 1rem 0; background: rgba(88,166,255,.05); }
ul, ol { margin: .5rem 0 .5rem 1.5rem; }
li { margin: .25rem 0; }
hr { border: none; border-top: 1px solid var(--border); margin: 2rem 0; }
CSSEOF

for md_file in "$DOCS_DIR"/*.md; do
  [ -f "$md_file" ] || continue

  filename=$(basename "$md_file" .md)

  [ "$filename" = "context" ] && continue

  echo "Converting $md_file → $OUTPUT_DIR/$filename/index.html"
  mkdir -p "$OUTPUT_DIR/$filename"

  pandoc "$md_file" \
    --from gfm \
    --to html5 \
    --standalone \
    --css "../style.css" \
    --metadata title="AgiBank QA Challenge" \
    -o "$OUTPUT_DIR/$filename/index.html"
done

echo "Docs built in $OUTPUT_DIR"
