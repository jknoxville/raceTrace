set terminal epslatex
set output 'eps1.eps'
set datafile separator ","

set yrange [0:2500]
set ylabel 'latency (ms)' tc lt 1

set y2range [0:100]
set y2tics 10 nomirror tc lt 2
set y2label 'success rate ()' tc lt 2
plot 'x.csv' using 1:2 title 'latency' with line lt 1, 'x.csv' using 1:7 title 'success rate' with line lt 2