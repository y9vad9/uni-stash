CXX = g++
CXXFLAGS = -std=c++17 -Wall -Wextra

LAB1_SRC = lab1/lab1_yaroshchuk_pi_22_1.cpp
LAB1_BIN = build/lab1

LAB2_SRC = lab2/lab2_yaroshchuk_pi_22_1.cpp
LAB2_BIN = build/lab2

LAB3_SRC = lab3/lab3_yaroshchuk_pi_22_1.cpp
LAB3_BIN = build/lab3

LAB4_SRC = lab4/lab4_yaroshchuk_pi_22_1.cpp
LAB4_BIN = build/lab4

LAB5_SRC = lab5/lab5_yaroshchuk_pi_22_1.cpp
LAB5_BIN = build/lab5

LAB6_SRC = lab6/lab6_yaroshchuk_pi_22_1.cpp
LAB6_BIN = build/lab6

all: clean lab1 lab2 lab3 lab4 lab5 lab6

lab1: $(LAB1_BIN)
lab2: $(LAB2_BIN)
lab3: $(LAB3_BIN)
lab4: $(LAB4_BIN)
lab5: $(LAB5_BIN)
lab6: $(LAB6_BIN)

$(LAB1_BIN): $(LAB1_SRC) | build_dir
	$(CXX) $(CXXFLAGS) -o $(LAB1_BIN) $(LAB1_SRC)

$(LAB2_BIN): $(LAB2_SRC) | build_dir
	$(CXX) $(CXXFLAGS) -o $(LAB2_BIN) $(LAB2_SRC)

$(LAB3_BIN): $(LAB3_SRC) | build_dir
	$(CXX) $(CXXFLAGS) -o $(LAB3_BIN) $(LAB3_SRC)

$(LAB4_BIN): $(LAB4_SRC) | build_dir
	$(CXX) $(CXXFLAGS) -o $(LAB4_BIN) $(LAB4_SRC)

$(LAB5_BIN): $(LAB5_SRC) | build_dir
	$(CXX) $(CXXFLAGS) -o $(LAB5_BIN) $(LAB5_SRC)

$(LAB6_BIN): $(LAB6_SRC) | build_dir
	$(CXX) $(CXXFLAGS) -o $(LAB6_BIN) $(LAB6_SRC)

build_dir:
	mkdir -p build

clean:
	rm -rf build
