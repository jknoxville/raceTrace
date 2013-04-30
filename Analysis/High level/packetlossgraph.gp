set terminal epslatex
set output 'packetloss.eps'
set datafile separator ","

set yrange [0:100]
set ytics 10 nomirror tc lt 1
set ylabel 'frequency' tc lt 1

set xlabel 'latency (ms)'
plot 'packetlossfinal.csv' using 1:2 title 'latency' with line lt 1, 'packetlossfinal.csv' using 1:3 title 'success rate' with line lt 2