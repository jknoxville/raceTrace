set terminal epslatex
set output 'eps1.eps'
set datafile separator ","

set yrange [0:3000]
set ytics 500 nomirror tc lt 1
set ylabel 'latency (ms)' tc lt 1

set y2range [0:100]
set y2tics 10 nomirror tc lt 2
set y2label 'success rate ()' tc lt 2

set xlabel 'session size (devices)'
plot 'finalscale.csv' using 1:2 title 'latency' with line lt 1, 'finalscale.csv' using 1:4 title 'success rate' with line lt 2