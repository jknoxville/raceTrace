set terminal epslatex
set output 'packetloss.eps'
set datafile separator ","

set xrange [0:5000]
set xtics 1000 nomirror tc lt 1
set xlabel 'Latency (ms)' tc lt 1

set yrange [0:100]
set ytics 10 nomirror tc lt 1
set ylabel 'Cumulative proportion of data points (\%)' tc lt 1

set key outside

plot 'packetlossfinal.csv' using 1:2 title 'C 10\%' with line lt 1, 'packetlossfinal.csv' using 1:7 title 'P 10\%' with line lt 2, 'packetlossfinal.csv' using 1:3 title 'C 20\%' with line lt 1, 'packetlossfinal.csv' using 1:8 title 'P 20\%' with line lt 2, 'packetlossfinal.csv' using 1:4 title 'C 30\%' with line lt 1, 'packetlossfinal.csv' using 1:9 title 'P 30\%' with line lt 2, 'packetlossfinal.csv' using 1:5 title 'C 40\%' with line lt 1, 'packetlossfinal.csv' using 1:10 title 'P 40\%' with line lt 2, 'packetlossfinal.csv' using 1:6 title 'C 50\%' with line lt 1, 'packetlossfinal.csv' using 1:11 title 'P 50\%' with line lt 2