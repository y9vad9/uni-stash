CXX = g++
CXXFLAGS = -std=c++17 -Wall -Wextra

LAB1_SRC = lab1/lab1_yaroshchuk_pi_22_1.cpp
LAB1_BIN = build/lab1

LAB2_SRC = lab2/lab2_yaroshchuk_pi_22_1.cpp
LAB2_BIN = build/lab2

LAB3_SRC = lab3/lab3_yaroshchuk_pi_22_1.cpp
LAB3_BIN = build/lab3

all: lab1 lab2 lab3

lab1: $(LAB1_BIN)
lab2: $(LAB2_BIN)
lab3: $(LAB3_BIN)

$(LAB1_BIN): $(LAB1_SRC) | build_dir
	$(CXX) $(CXXFLAGS) -o $(LAB1_BIN) $(LAB1_SRC)

$(LAB2_BIN): $(LAB2_SRC) | build_dir
	$(CXX) $(CXXFLAGS) -o $(LAB2_BIN) $(LAB2_SRC)

$(LAB3_BIN): $(LAB3_SRC) | build_dir
	$(CXX) $(CXXFLAGS) -o $(LAB3_BIN) $(LAB3_SRC)

build_dir:
	mkdir -p build

clean:
	rm -rf build
