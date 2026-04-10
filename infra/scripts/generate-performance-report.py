#!/usr/bin/env python3
"""
Generates a rich, visual HTML performance report from JMeter CSV results.
Usage: python generate-performance-report.py <csv_file> <output_dir> [test_name]
"""

import csv
import os
import sys
from collections import defaultdict
from datetime import datetime, timezone
from statistics import mean, median

def parse_csv(csv_path):
    rows = []
    with open(csv_path, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            rows.append(row)
    return rows

def percentile(sorted_list, p):
    if not sorted_list:
        return 0
    k = (len(sorted_list) - 1) * (p / 100)
    f = int(k)
    c = f + 1
    if c >= len(sorted_list):
        return sorted_list[f]
    return sorted_list[f] + (k - f) * (sorted_list[c] - sorted_list[f])

def calc_metrics(rows):
    total = len(rows)
    if total == 0:
        return {}

    success = sum(1 for r in rows if r.get("success", "").lower() == "true")
    failed = total - success
    error_rate = (failed / total) * 100 if total > 0 else 0

    elapsed_times = sorted([int(r["elapsed"]) for r in rows if r.get("elapsed")])
    latencies = sorted([int(r["Latency"]) for r in rows if r.get("Latency")])
    connect_times = sorted([int(r["Connect"]) for r in rows if r.get("Connect")])
    bytes_recv = [int(r["bytes"]) for r in rows if r.get("bytes")]

    timestamps = [int(r["timeStamp"]) for r in rows if r.get("timeStamp")]
    min_ts = min(timestamps) if timestamps else 0
    max_ts = max(timestamps) if timestamps else 0
    duration_s = (max_ts - min_ts) / 1000 if max_ts > min_ts else 1
    throughput = total / duration_s

    return {
        "total": total,
        "success": success,
        "failed": failed,
        "error_rate": error_rate,
        "throughput": throughput,
        "duration_s": duration_s,
        "avg_rt": mean(elapsed_times) if elapsed_times else 0,
        "median_rt": median(elapsed_times) if elapsed_times else 0,
        "min_rt": min(elapsed_times) if elapsed_times else 0,
        "max_rt": max(elapsed_times) if elapsed_times else 0,
        "p90_rt": percentile(elapsed_times, 90),
        "p95_rt": percentile(elapsed_times, 95),
        "p99_rt": percentile(elapsed_times, 99),
        "avg_latency": mean(latencies) if latencies else 0,
        "avg_connect": mean(connect_times) if connect_times else 0,
        "total_bytes": sum(bytes_recv),
        "avg_bytes": mean(bytes_recv) if bytes_recv else 0,
        "min_ts": min_ts,
        "max_ts": max_ts,
    }

def calc_per_label(rows):
    by_label = defaultdict(list)
    for r in rows:
        label = r.get("label", "Unknown")
        by_label[label].append(r)

    results = {}
    for label, label_rows in sorted(by_label.items()):
        results[label] = calc_metrics(label_rows)
    return results

def calc_time_series(rows, bucket_s=5):
    if not rows:
        return [], [], [], []
    timestamps = [int(r["timeStamp"]) for r in rows]
    min_ts = min(timestamps)
    buckets = defaultdict(lambda: {"count": 0, "elapsed": [], "errors": 0})
    for r in rows:
        ts = int(r["timeStamp"])
        bucket = ((ts - min_ts) // (bucket_s * 1000)) * bucket_s
        buckets[bucket]["count"] += 1
        buckets[bucket]["elapsed"].append(int(r.get("elapsed", 0)))
        if r.get("success", "").lower() != "true":
            buckets[bucket]["errors"] += 1

    sorted_buckets = sorted(buckets.keys())
    labels = [f"{b}s" for b in sorted_buckets]
    throughput = [buckets[b]["count"] / bucket_s for b in sorted_buckets]
    avg_rt = [mean(buckets[b]["elapsed"]) if buckets[b]["elapsed"] else 0 for b in sorted_buckets]
    error_pct = [
        (buckets[b]["errors"] / buckets[b]["count"] * 100) if buckets[b]["count"] > 0 else 0
        for b in sorted_buckets
    ]
    return labels, throughput, avg_rt, error_pct

def verdict_emoji(error_rate, p90):
    if error_rate < 1 and p90 < 2000:
        return "✅", "Excelente", "#10b981"
    elif error_rate < 5 and p90 < 3000:
        return "🟡", "Aceitável", "#f59e0b"
    elif error_rate < 10 and p90 < 5000:
        return "🟠", "Atenção", "#f97316"
    else:
        return "🔴", "Crítico", "#ef4444"

def fmt_ms(ms):
    if ms < 1000:
        return f"{ms:.0f}ms"
    return f"{ms / 1000:.2f}s"

def fmt_bytes(b):
    if b < 1024:
        return f"{b:.0f} B"
    elif b < 1024 * 1024:
        return f"{b / 1024:.1f} KB"
    else:
        return f"{b / (1024 * 1024):.2f} MB"

def fmt_number(n):
    if n >= 1000000:
        return f"{n / 1000000:.1f}M"
    elif n >= 1000:
        return f"{n / 1000:.1f}K"
    return f"{n:.0f}"

TRANSACTION_EMOJIS = {
    "01 - Home Page": "🏠",
    "02 - Find Flights": "🔍",
    "03 - Choose Flight": "✈️",
    "04 - Confirm Purchase": "✅",
    "TC - Flight Purchase": "🔄",
}

def get_emoji(label):
    for key, emoji in TRANSACTION_EMOJIS.items():
        if key in label:
            return emoji
    return "📊"

def generate_html(metrics, per_label, time_labels, throughput_ts, avg_rt_ts, error_ts, test_name, csv_path):
    v_emoji, v_text, v_color = verdict_emoji(metrics["error_rate"], metrics["p90_rt"])
    now = datetime.now(timezone.utc).strftime("%d/%m/%Y %H:%M UTC")
    start_time = datetime.fromtimestamp(metrics["min_ts"] / 1000, tz=timezone.utc).strftime("%d/%m/%Y %H:%M:%S UTC") if metrics["min_ts"] else "N/A"

    per_label_filtered = {k: v for k, v in per_label.items() if "TC -" not in k}
    tc_metrics = {k: v for k, v in per_label.items() if "TC -" in k}

    label_names = [f"{get_emoji(l)} {l}" for l in per_label_filtered.keys()]
    label_avg = [v["avg_rt"] for v in per_label_filtered.values()]
    label_p90 = [v["p90_rt"] for v in per_label_filtered.values()]
    label_p95 = [v["p95_rt"] for v in per_label_filtered.values()]
    label_errors = [v["error_rate"] for v in per_label_filtered.values()]

    html = f"""<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>✈️ Performance Report — {test_name}</title>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<style>
  :root {{
    --bg: #0f172a;
    --surface: #1e293b;
    --surface2: #334155;
    --text: #f1f5f9;
    --text-muted: #94a3b8;
    --accent: #3b82f6;
    --green: #10b981;
    --yellow: #f59e0b;
    --orange: #f97316;
    --red: #ef4444;
    --purple: #8b5cf6;
    --pink: #ec4899;
    --cyan: #06b6d4;
  }}
  * {{ margin: 0; padding: 0; box-sizing: border-box; }}
  body {{
    font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
    background: var(--bg);
    color: var(--text);
    line-height: 1.6;
    min-height: 100vh;
  }}
  .container {{ max-width: 1400px; margin: 0 auto; padding: 2rem; }}

  /* Header */
  .header {{
    text-align: center;
    padding: 3rem 2rem;
    background: linear-gradient(135deg, #1e3a5f 0%, #0f172a 50%, #1a1a2e 100%);
    border-bottom: 3px solid var(--accent);
    margin-bottom: 2rem;
  }}
  .header h1 {{
    font-size: 2.5rem;
    background: linear-gradient(135deg, #60a5fa, #a78bfa, #f472b6);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin-bottom: 0.5rem;
  }}
  .header .subtitle {{ color: var(--text-muted); font-size: 1.1rem; }}
  .header .meta {{
    display: flex;
    justify-content: center;
    gap: 2rem;
    margin-top: 1.5rem;
    flex-wrap: wrap;
  }}
  .header .meta span {{
    background: var(--surface);
    padding: 0.4rem 1rem;
    border-radius: 999px;
    font-size: 0.85rem;
    color: var(--text-muted);
  }}

  /* Verdict Banner */
  .verdict {{
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 1rem;
    padding: 1.5rem;
    border-radius: 16px;
    margin-bottom: 2rem;
    font-size: 1.3rem;
    font-weight: 600;
    border: 2px solid {v_color};
    background: {v_color}15;
  }}
  .verdict-emoji {{ font-size: 2rem; }}

  /* Cards Grid */
  .cards {{
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 1rem;
    margin-bottom: 2rem;
  }}
  .card {{
    background: var(--surface);
    border-radius: 16px;
    padding: 1.5rem;
    text-align: center;
    border: 1px solid var(--surface2);
    transition: transform 0.2s, box-shadow 0.2s;
  }}
  .card:hover {{
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0,0,0,0.3);
  }}
  .card .icon {{ font-size: 1.8rem; margin-bottom: 0.5rem; }}
  .card .value {{
    font-size: 2rem;
    font-weight: 700;
    background: linear-gradient(135deg, var(--accent), var(--cyan));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }}
  .card .label {{ color: var(--text-muted); font-size: 0.85rem; margin-top: 0.3rem; }}
  .card.green .value {{ background: linear-gradient(135deg, var(--green), #34d399); -webkit-background-clip: text; background-clip: text; }}
  .card.red .value {{ background: linear-gradient(135deg, var(--red), var(--orange)); -webkit-background-clip: text; background-clip: text; }}
  .card.purple .value {{ background: linear-gradient(135deg, var(--purple), var(--pink)); -webkit-background-clip: text; background-clip: text; }}
  .card.yellow .value {{ background: linear-gradient(135deg, var(--yellow), var(--orange)); -webkit-background-clip: text; background-clip: text; }}

  /* Sections */
  .section {{
    background: var(--surface);
    border-radius: 16px;
    padding: 2rem;
    margin-bottom: 2rem;
    border: 1px solid var(--surface2);
  }}
  .section h2 {{
    font-size: 1.4rem;
    margin-bottom: 1.5rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }}
  .section h2::after {{
    content: '';
    flex: 1;
    height: 1px;
    background: var(--surface2);
    margin-left: 1rem;
  }}

  /* Tables */
  table {{
    width: 100%;
    border-collapse: collapse;
    font-size: 0.9rem;
  }}
  th {{
    text-align: left;
    padding: 0.8rem 1rem;
    background: var(--surface2);
    color: var(--text-muted);
    font-weight: 600;
    text-transform: uppercase;
    font-size: 0.75rem;
    letter-spacing: 0.05em;
  }}
  th:first-child {{ border-radius: 8px 0 0 8px; }}
  th:last-child {{ border-radius: 0 8px 8px 0; }}
  td {{
    padding: 0.8rem 1rem;
    border-bottom: 1px solid var(--surface2);
  }}
  tr:last-child td {{ border-bottom: none; }}
  tr:hover td {{ background: var(--bg); }}
  .text-right {{ text-align: right; }}
  .text-center {{ text-align: center; }}

  /* Badges */
  .badge {{
    display: inline-block;
    padding: 0.2rem 0.6rem;
    border-radius: 999px;
    font-size: 0.75rem;
    font-weight: 600;
  }}
  .badge-green {{ background: #10b98120; color: var(--green); }}
  .badge-yellow {{ background: #f59e0b20; color: var(--yellow); }}
  .badge-red {{ background: #ef444420; color: var(--red); }}

  /* Charts */
  .charts-grid {{
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2rem;
  }}
  .chart-card {{
    background: var(--surface);
    border-radius: 16px;
    padding: 1.5rem;
    border: 1px solid var(--surface2);
  }}
  .chart-card h3 {{
    font-size: 1rem;
    margin-bottom: 1rem;
    color: var(--text-muted);
  }}

  /* Percentile Bar */
  .percentile-bar {{
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-bottom: 0.8rem;
  }}
  .percentile-bar .label {{ width: 60px; font-size: 0.85rem; color: var(--text-muted); }}
  .percentile-bar .bar-bg {{
    flex: 1;
    height: 24px;
    background: var(--bg);
    border-radius: 12px;
    overflow: hidden;
    position: relative;
  }}
  .percentile-bar .bar-fill {{
    height: 100%;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding-right: 8px;
    font-size: 0.75rem;
    font-weight: 600;
    min-width: 50px;
    transition: width 1s ease;
  }}

  /* Footer */
  .footer {{
    text-align: center;
    padding: 2rem;
    color: var(--text-muted);
    font-size: 0.85rem;
  }}
  .footer a {{ color: var(--accent); text-decoration: none; }}
  .footer a:hover {{ text-decoration: underline; }}

  /* Flow */
  .flow {{
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    flex-wrap: wrap;
    margin: 1.5rem 0;
  }}
  .flow-step {{
    background: var(--bg);
    padding: 0.8rem 1.2rem;
    border-radius: 12px;
    text-align: center;
    font-size: 0.85rem;
    border: 1px solid var(--surface2);
  }}
  .flow-step .step-emoji {{ font-size: 1.5rem; display: block; margin-bottom: 0.3rem; }}
  .flow-arrow {{ color: var(--text-muted); font-size: 1.2rem; }}

  /* Config table */
  .config-grid {{
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1rem;
  }}
  .config-item {{
    display: flex;
    align-items: center;
    gap: 0.8rem;
    padding: 0.8rem;
    background: var(--bg);
    border-radius: 10px;
  }}
  .config-item .cfg-icon {{ font-size: 1.3rem; }}
  .config-item .cfg-label {{ color: var(--text-muted); font-size: 0.8rem; }}
  .config-item .cfg-value {{ font-weight: 600; }}

  @media (max-width: 768px) {{
    .container {{ padding: 1rem; }}
    .header h1 {{ font-size: 1.8rem; }}
    .cards {{ grid-template-columns: repeat(2, 1fr); }}
    .charts-grid {{ grid-template-columns: 1fr; }}
  }}
</style>
</head>
<body>

<div class="header">
  <h1>✈️ Performance Report</h1>
  <p class="subtitle">{test_name} — BlazeDemo Flight Purchase Flow</p>
  <div class="meta">
    <span>📅 {now}</span>
    <span>⏱️ Início: {start_time}</span>
    <span>🔧 JMeter 5.6.3</span>
    <span>🖥️ Self-hosted Runner (VPS)</span>
  </div>
</div>

<div class="container">

  <!-- Verdict -->
  <div class="verdict">
    <span class="verdict-emoji">{v_emoji}</span>
    <span>Veredicto: <span style="color:{v_color}">{v_text}</span> — Error Rate: {metrics['error_rate']:.2f}% | p90: {fmt_ms(metrics['p90_rt'])} | Throughput: {metrics['throughput']:.1f} req/s</span>
  </div>

  <!-- KPI Cards -->
  <div class="cards">
    <div class="card">
      <div class="icon">📦</div>
      <div class="value">{fmt_number(metrics['total'])}</div>
      <div class="label">Total de Requisições</div>
    </div>
    <div class="card green">
      <div class="icon">✅</div>
      <div class="value">{fmt_number(metrics['success'])}</div>
      <div class="label">Sucesso</div>
    </div>
    <div class="card red">
      <div class="icon">❌</div>
      <div class="value">{fmt_number(metrics['failed'])}</div>
      <div class="label">Falhas</div>
    </div>
    <div class="card {'green' if metrics['error_rate'] < 1 else 'yellow' if metrics['error_rate'] < 5 else 'red'}">
      <div class="icon">📉</div>
      <div class="value">{metrics['error_rate']:.2f}%</div>
      <div class="label">Taxa de Erro</div>
    </div>
    <div class="card purple">
      <div class="icon">🚀</div>
      <div class="value">{metrics['throughput']:.1f}</div>
      <div class="label">Req/s (Throughput)</div>
    </div>
    <div class="card">
      <div class="icon">⏱️</div>
      <div class="value">{fmt_ms(metrics['avg_rt'])}</div>
      <div class="label">Tempo Médio de Resposta</div>
    </div>
    <div class="card yellow">
      <div class="icon">📊</div>
      <div class="value">{fmt_ms(metrics['p90_rt'])}</div>
      <div class="label">p90 Response Time</div>
    </div>
    <div class="card">
      <div class="icon">⏳</div>
      <div class="value">{metrics['duration_s']:.0f}s</div>
      <div class="label">Duração Total</div>
    </div>
  </div>

  <!-- Test Flow -->
  <div class="section">
    <h2>🔄 Fluxo Testado</h2>
    <div class="flow">
      <div class="flow-step"><span class="step-emoji">🏠</span>Home Page<br><small>GET /</small></div>
      <span class="flow-arrow">→</span>
      <div class="flow-step"><span class="step-emoji">🔍</span>Buscar Voos<br><small>POST /reserve.php</small></div>
      <span class="flow-arrow">→</span>
      <div class="flow-step"><span class="step-emoji">✈️</span>Escolher Voo<br><small>POST /purchase.php</small></div>
      <span class="flow-arrow">→</span>
      <div class="flow-step"><span class="step-emoji">✅</span>Confirmar Compra<br><small>POST /confirmation.php</small></div>
    </div>
  </div>

  <!-- Percentile Distribution -->
  <div class="section">
    <h2>📊 Distribuição de Percentis (Response Time)</h2>
    <div style="max-width: 700px;">
      <div class="percentile-bar">
        <span class="label">Min</span>
        <div class="bar-bg"><div class="bar-fill" style="width:{max(5, metrics['min_rt']/metrics['max_rt']*100)}%;background:var(--green);">{fmt_ms(metrics['min_rt'])}</div></div>
      </div>
      <div class="percentile-bar">
        <span class="label">Mediana</span>
        <div class="bar-bg"><div class="bar-fill" style="width:{max(10, metrics['median_rt']/metrics['max_rt']*100)}%;background:var(--cyan);">{fmt_ms(metrics['median_rt'])}</div></div>
      </div>
      <div class="percentile-bar">
        <span class="label">Média</span>
        <div class="bar-bg"><div class="bar-fill" style="width:{max(10, metrics['avg_rt']/metrics['max_rt']*100)}%;background:var(--accent);">{fmt_ms(metrics['avg_rt'])}</div></div>
      </div>
      <div class="percentile-bar">
        <span class="label">p90</span>
        <div class="bar-bg"><div class="bar-fill" style="width:{max(15, metrics['p90_rt']/metrics['max_rt']*100)}%;background:var(--yellow);">{fmt_ms(metrics['p90_rt'])}</div></div>
      </div>
      <div class="percentile-bar">
        <span class="label">p95</span>
        <div class="bar-bg"><div class="bar-fill" style="width:{max(20, metrics['p95_rt']/metrics['max_rt']*100)}%;background:var(--orange);">{fmt_ms(metrics['p95_rt'])}</div></div>
      </div>
      <div class="percentile-bar">
        <span class="label">p99</span>
        <div class="bar-bg"><div class="bar-fill" style="width:{max(25, metrics['p99_rt']/metrics['max_rt']*100)}%;background:var(--red);">{fmt_ms(metrics['p99_rt'])}</div></div>
      </div>
      <div class="percentile-bar">
        <span class="label">Max</span>
        <div class="bar-bg"><div class="bar-fill" style="width:100%;background:linear-gradient(90deg, var(--red), var(--pink));">{fmt_ms(metrics['max_rt'])}</div></div>
      </div>
    </div>
  </div>

  <!-- Charts -->
  <div class="charts-grid">
    <div class="chart-card">
      <h3>📈 Throughput ao Longo do Tempo (req/s)</h3>
      <canvas id="throughputChart"></canvas>
    </div>
    <div class="chart-card">
      <h3>⏱️ Response Time Médio ao Longo do Tempo</h3>
      <canvas id="rtChart"></canvas>
    </div>
    <div class="chart-card">
      <h3>📊 Response Time por Transação (Avg vs p90 vs p95)</h3>
      <canvas id="labelChart"></canvas>
    </div>
    <div class="chart-card">
      <h3>📉 Taxa de Erro ao Longo do Tempo (%)</h3>
      <canvas id="errorChart"></canvas>
    </div>
  </div>

  <!-- Per-Transaction Table -->
  <div class="section">
    <h2>📋 Resultados por Transação</h2>
    <div style="overflow-x: auto;">
    <table>
      <thead>
        <tr>
          <th>Transação</th>
          <th class="text-right">Samples</th>
          <th class="text-right">Avg</th>
          <th class="text-right">Mediana</th>
          <th class="text-right">p90</th>
          <th class="text-right">p95</th>
          <th class="text-right">p99</th>
          <th class="text-right">Min</th>
          <th class="text-right">Max</th>
          <th class="text-right">Error %</th>
          <th class="text-right">Req/s</th>
        </tr>
      </thead>
      <tbody>"""

    for label, m in per_label.items():
        emoji = get_emoji(label)
        err_badge = "badge-green" if m["error_rate"] < 1 else "badge-yellow" if m["error_rate"] < 5 else "badge-red"
        is_tc = "TC -" in label
        row_style = ' style="font-weight:600;background:var(--bg);"' if is_tc else ""
        html += f"""
        <tr{row_style}>
          <td>{emoji} {label}</td>
          <td class="text-right">{fmt_number(m['total'])}</td>
          <td class="text-right">{fmt_ms(m['avg_rt'])}</td>
          <td class="text-right">{fmt_ms(m['median_rt'])}</td>
          <td class="text-right">{fmt_ms(m['p90_rt'])}</td>
          <td class="text-right">{fmt_ms(m['p95_rt'])}</td>
          <td class="text-right">{fmt_ms(m['p99_rt'])}</td>
          <td class="text-right">{fmt_ms(m['min_rt'])}</td>
          <td class="text-right">{fmt_ms(m['max_rt'])}</td>
          <td class="text-right"><span class="badge {err_badge}">{m['error_rate']:.2f}%</span></td>
          <td class="text-right">{m['throughput']:.1f}</td>
        </tr>"""

    html += f"""
      </tbody>
    </table>
    </div>
  </div>

  <!-- Network & Data -->
  <div class="section">
    <h2>🌐 Rede e Dados</h2>
    <div class="cards" style="margin-bottom:0;">
      <div class="card">
        <div class="icon">📡</div>
        <div class="value">{fmt_ms(metrics['avg_latency'])}</div>
        <div class="label">Latência Média</div>
      </div>
      <div class="card">
        <div class="icon">🔌</div>
        <div class="value">{fmt_ms(metrics['avg_connect'])}</div>
        <div class="label">Tempo Médio de Conexão</div>
      </div>
      <div class="card">
        <div class="icon">📥</div>
        <div class="value">{fmt_bytes(metrics['total_bytes'])}</div>
        <div class="label">Total de Dados Recebidos</div>
      </div>
      <div class="card">
        <div class="icon">📄</div>
        <div class="value">{fmt_bytes(metrics['avg_bytes'])}</div>
        <div class="label">Tamanho Médio da Resposta</div>
      </div>
    </div>
  </div>

  <!-- Test Config -->
  <div class="section">
    <h2>⚙️ Configuração do Teste</h2>
    <div class="config-grid">
      <div class="config-item">
        <span class="cfg-icon">🧵</span>
        <div><div class="cfg-label">Threads (Usuários)</div><div class="cfg-value">150</div></div>
      </div>
      <div class="config-item">
        <span class="cfg-icon">⏱️</span>
        <div><div class="cfg-label">Ramp-up</div><div class="cfg-value">60 segundos</div></div>
      </div>
      <div class="config-item">
        <span class="cfg-icon">⏳</span>
        <div><div class="cfg-label">Duração</div><div class="cfg-value">240 segundos</div></div>
      </div>
      <div class="config-item">
        <span class="cfg-icon">🌐</span>
        <div><div class="cfg-label">Aplicação Alvo</div><div class="cfg-value">blazedemo.com</div></div>
      </div>
      <div class="config-item">
        <span class="cfg-icon">⏰</span>
        <div><div class="cfg-label">Think Time</div><div class="cfg-value">500ms ± 500ms</div></div>
      </div>
      <div class="config-item">
        <span class="cfg-icon">📊</span>
        <div><div class="cfg-label">Backend Listener</div><div class="cfg-value">InfluxDB (tempo real)</div></div>
      </div>
    </div>
  </div>

</div>

<div class="footer">
  <p>✈️ Relatório gerado automaticamente a partir dos resultados JMeter</p>
  <p>
    <a href="https://rennangimenez.com/grafana/">📈 Grafana Dashboards</a> ·
    <a href="https://github.com/rennangimenez/agibank-qa-automation-challenge">🔗 GitHub</a> ·
    <a href="https://rennangimenez.com/agibank-challenge/">📊 Todos os Reports</a>
  </p>
  <p style="margin-top:0.5rem;">Feito com ☕ por <strong>Rennan Gimenez</strong></p>
</div>

<script>
Chart.defaults.color = '#94a3b8';
Chart.defaults.borderColor = '#334155';
Chart.defaults.font.family = "'Segoe UI', system-ui, sans-serif";

const timeLabels = {time_labels};
const throughputData = {[round(v, 2) for v in throughput_ts]};
const avgRtData = {[round(v, 1) for v in avg_rt_ts]};
const errorData = {[round(v, 2) for v in error_ts]};

const labelNames = {label_names};
const labelAvg = {[round(v, 1) for v in label_avg]};
const labelP90 = {[round(v, 1) for v in label_p90]};
const labelP95 = {[round(v, 1) for v in label_p95]};
const labelErrors = {[round(v, 2) for v in label_errors]};

new Chart(document.getElementById('throughputChart'), {{
  type: 'line',
  data: {{
    labels: timeLabels,
    datasets: [{{
      label: 'Throughput (req/s)',
      data: throughputData,
      borderColor: '#3b82f6',
      backgroundColor: '#3b82f620',
      fill: true,
      tension: 0.3,
      pointRadius: 0,
    }}]
  }},
  options: {{
    responsive: true,
    plugins: {{ legend: {{ display: false }} }},
    scales: {{
      x: {{ display: true, ticks: {{ maxTicksLimit: 15 }} }},
      y: {{ beginAtZero: true, title: {{ display: true, text: 'req/s' }} }}
    }}
  }}
}});

new Chart(document.getElementById('rtChart'), {{
  type: 'line',
  data: {{
    labels: timeLabels,
    datasets: [{{
      label: 'Avg Response Time (ms)',
      data: avgRtData,
      borderColor: '#f59e0b',
      backgroundColor: '#f59e0b20',
      fill: true,
      tension: 0.3,
      pointRadius: 0,
    }}]
  }},
  options: {{
    responsive: true,
    plugins: {{ legend: {{ display: false }} }},
    scales: {{
      x: {{ display: true, ticks: {{ maxTicksLimit: 15 }} }},
      y: {{ beginAtZero: true, title: {{ display: true, text: 'ms' }} }}
    }}
  }}
}});

new Chart(document.getElementById('labelChart'), {{
  type: 'bar',
  data: {{
    labels: labelNames,
    datasets: [
      {{ label: 'Avg', data: labelAvg, backgroundColor: '#3b82f6', borderRadius: 4 }},
      {{ label: 'p90', data: labelP90, backgroundColor: '#f59e0b', borderRadius: 4 }},
      {{ label: 'p95', data: labelP95, backgroundColor: '#ef4444', borderRadius: 4 }}
    ]
  }},
  options: {{
    responsive: true,
    plugins: {{ legend: {{ position: 'top' }} }},
    scales: {{
      y: {{ beginAtZero: true, title: {{ display: true, text: 'ms' }} }}
    }}
  }}
}});

new Chart(document.getElementById('errorChart'), {{
  type: 'line',
  data: {{
    labels: timeLabels,
    datasets: [{{
      label: 'Error Rate (%)',
      data: errorData,
      borderColor: '#ef4444',
      backgroundColor: '#ef444420',
      fill: true,
      tension: 0.3,
      pointRadius: 0,
    }}]
  }},
  options: {{
    responsive: true,
    plugins: {{ legend: {{ display: false }} }},
    scales: {{
      x: {{ display: true, ticks: {{ maxTicksLimit: 15 }} }},
      y: {{ beginAtZero: true, title: {{ display: true, text: '%' }} }}
    }}
  }}
}});
</script>

</body>
</html>"""

    return html


def main():
    if len(sys.argv) < 3:
        print(f"Usage: {sys.argv[0]} <csv_file> <output_dir> [test_name]")
        sys.exit(1)

    csv_path = sys.argv[1]
    output_dir = sys.argv[2]
    test_name = sys.argv[3] if len(sys.argv) > 3 else "Performance Test"

    if not os.path.exists(csv_path):
        print(f"ERROR: CSV file not found: {csv_path}")
        sys.exit(1)

    print(f"📊 Parsing JMeter results: {csv_path}")
    rows = parse_csv(csv_path)
    print(f"   → {len(rows)} samples found")

    metrics = calc_metrics(rows)
    per_label = calc_per_label(rows)
    time_labels, throughput_ts, avg_rt_ts, error_ts = calc_time_series(rows, bucket_s=5)

    print(f"   → Throughput: {metrics['throughput']:.1f} req/s")
    print(f"   → Error Rate: {metrics['error_rate']:.2f}%")
    print(f"   → Avg RT: {metrics['avg_rt']:.0f}ms | p90: {metrics['p90_rt']:.0f}ms")

    html = generate_html(metrics, per_label, time_labels, throughput_ts, avg_rt_ts, error_ts, test_name, csv_path)

    os.makedirs(output_dir, exist_ok=True)
    output_path = os.path.join(output_dir, "index.html")
    with open(output_path, "w", encoding="utf-8") as f:
        f.write(html)

    print(f"✅ Report generated: {output_path}")


if __name__ == "__main__":
    main()
