package com.mathpar.students.OLD.llp2.students;

import mpi.*;
import com.mathpar.number.*;
import com.mathpar.polynom.Polynom;
import com.mathpar.number.SubsetZ;
import com.mathpar.students.OLD.llp2.student.message.ParallelDebug;

/**
 *
 * @author Averina
 */
public class Averina {

	// mpirun C java -cp /home/averina/parca/build/web/WEB-INF/classes/
	// -Djava.library.path=$LD_LIBRARY_PATH helloworld//!!!! MPI.AverinaKurs

	/**
	 * метод, параллельно умножающий 2 полинома, исходя из минимального кванта
	 * их разбиения
	 *
	 * @param s1
	 *            - 1-й полином
	 * @param s2
	 *            - 2-ой полином
	 * @param quant
	 *            - квант разбиения полиномов
	 * @param mpi
	 *            - представитель класса MPI
	 * @return полином, равный произведению s1*s2
	 * @throws MPIException
	 */
	public Polynom multiplyPolynomPar(Polynom s1, Polynom s2, int quant, MPI mpi)
			throws MPIException {
		int myrank =  MPI.COMM_WORLD.getRank(); // номер процессора
		int p = MPI.COMM_WORLD.getSize(); // количество процессоров 3*x + 2*x*x +
										// 4*x*x*y + x*y + 5*y*y + 8*y*y*x +
										// 2*x*x*x*x +
		ParallelDebug pred = new ParallelDebug( MPI.COMM_WORLD);
		int q1;
		int q2;
		int ness;
		int kp0 = (int) (Math.sqrt((double) p));
		int newp = kp0 * kp0;// в случае когда проц меньше чем нужно это новое
								// кол-во работающих процесоро
		Ring ring = Ring.ringR64xyzt;
		Polynom ab = new Polynom("0", ring);
		if (quant >= s1.coeffs.length && quant >= s2.coeffs.length) {
			if (myrank == 0) {
				pred.paddEvent("квант разбиения много меньше длины полиномов",
						"процессор 0 вычисляет произведение");
				pred.generateDebugLog();
				return s1.multiply(s2, ring);
			}

			else {
				pred.paddEvent("процесор" + myrank
						+ " не участвует в вычислениях", "процессор свободен");
				pred.generateDebugLog();
				return new Polynom("0", ring);
			}
		}
		Polynom[] s1d = null;
		Polynom[] s2d = null;
		// программа для нулевого процессора
		if (myrank == 0) {
			if ((s1.coeffs.length) % quant == 0) {
				q1 = (s1.coeffs.length) / quant;
			} else {
				q1 = (s1.coeffs.length) / quant + 1;
			}
			if ((s2.coeffs.length) % quant == 0) {
				q2 = (s2.coeffs.length) / quant;
			} else {
				q2 = (s2.coeffs.length) / quant + 1;
			}
			System.out.println("length s1= " + s1.coeffs.length + "  my="
					+ myrank);
			System.out.println("length s2= " + s2.coeffs.length + "  my="
					+ myrank);
			// определяем необходимое кол-во процессоров
			ness = q1 * q2;
			if (ness == 3 || ness == 2 || ness == 1 || p == 2) {
				String ev1;
				String ev2;
				if (myrank == 0) {
					ev1 = "процессор получил полиномы s1=" + s1 + " ;" + "s2=";
					ev2 = "процессор вычисляет произведение";
				} else {
					ev1 = "процесоров слишком мало";
					ev2 = "вычисления происходят только на 0";
				}
				pred.paddEvent(ev1, ev2);
				pred.generateDebugLog();
				return s1.multiply(s2, ring);
			}
			Object sends = new Object[] { ness };
			for (int i = 1; i < p; i++) {
				pred.paddEvent(
						"процессор передает количество необходимых процессоров="
								+ ness, "процессор " + (i) + " принял");
				//!!!! MPI.COMM_WORLD.Send(sends, 0, 1, //!!!! MPI.OBJECT, i, 000);
				System.out.println("0 процессор отправил " + i + " процессору");
			}
			if (p < ness) {// если кол-во процессоров меньше чем нужно
				SubsetZ sub1 = new SubsetZ(new int[] { 0, s1.coeffs.length });
				SubsetZ sub2 = new SubsetZ(new int[] { 0, s2.coeffs.length });
				SubsetZ[] numS1 = sub1.divideOnParts(kp0);
				SubsetZ[] numS2 = sub2.divideOnParts(kp0);
				s1d = new Polynom[kp0];
				s2d = new Polynom[kp0];

				for (int i = 0; i < numS1.length; i++) {
					int end = numS1[i].toArray()[1] + 1;
					if (end > s1.coeffs.length) {
						end = s1.coeffs.length;
					}
					s1d[i] = s1.subPolynom(numS1[i].toArray()[0], end);
					System.out.println("s1[" + i + "] = "
							+ s1d[i].toString(ring));
				}
				for (int i = 0; i < numS2.length; i++) {
					int end = numS2[i].toArray()[1] + 1;
					if (end > s2.coeffs.length) {
						end = s2.coeffs.length;
					}
					s2d[i] = s2.subPolynom(numS2[i].toArray()[0], end);
					System.out.println("s2[" + i + "] = "
							+ s2d[i].toString(ring));
				}
			}

			if (p >= ness) {
				SubsetZ sub1 = new SubsetZ(new int[] { 0, s1.coeffs.length });
				SubsetZ sub2 = new SubsetZ(new int[] { 0, s2.coeffs.length });
				SubsetZ[] numS1 = sub1.divideOnParts(q1);
				SubsetZ[] numS2 = sub2.divideOnParts(q2);
				s1d = new Polynom[q1];
				s2d = new Polynom[q2];

				for (int i = 0; i < numS1.length; i++) {
					int end = numS1[i].toArray()[1] + 1;
					if (end > s1.coeffs.length) {
						end = s1.coeffs.length;
					}
					s1d[i] = s1.subPolynom(numS1[i].toArray()[0], end);
					System.out.println("s1[" + i + "] = " + s1d[i].toString());
				}
				for (int i = 0; i < numS2.length; i++) {
					int end = numS2[i].toArray()[1] + 1;
					if (end > s2.coeffs.length) {
						end = s2.coeffs.length;
					}
					s2d[i] = s2.subPolynom(numS2[i].toArray()[0], end);
					System.out.println("s2[" + i + "] = " + s2d[i].toString());
				}
			}

			Polynom a1 = s1d[0];
			Polynom b1 = s2d[0];
			ab = a1.multiply(b1, ring);
			// рассылка нулевым процессором каждому процессору(кроме 0)
			// соответствующих частей
			int k = 0;
			Object[] send = null;
			for (int j = 0; j < s1d.length; j++) {
				for (int r = 0; r < s2d.length; r++) {
					if ((j == 0) && (r == 0)) {
						continue;
					}
					pred.paddEvent("процессор передает подполиномы " + "s1="
							+ s1d[j] + " ;s2=" + s2d[r], "процессор " + (r + k)
							+ " начал вычисления");
					send = new Object[] { s1d[j], s2d[r] };
					//!!!! MPI.COMM_WORLD.Send(send, 0, 2, //!!!! MPI.OBJECT, r + k, 111);
					System.out.println("0 процессор отправил " + (r + k)
							+ " процессору");
				}
				k = k + s2d.length;
			}
		}

		// программа остальных процессоров
		else {
			Object[] nesss = new Object[1];
			//!!!! MPI.COMM_WORLD.Recv(nesss, 0, 1, //!!!! MPI.OBJECT, 0, 000);
			System.out.println("процессор " + myrank
					+ " принял кол-во процессоров");
			ness = (Integer) nesss[0];
			pred.paddEvent("процессор получил необходимое число процессоров = "
					+ ness, "процессор " + myrank + " принял число от 0");
			System.out.println("ness new = " + ness);

			Object[] m1 = new Object[2];
			if (p > ness) {// если кол-во проц больше чем нужно
				if (myrank < ness) {
					//!!!! MPI.COMM_WORLD.Recv(m1, 0, 2, //!!!! MPI.OBJECT, 0, 111);
					// перемножаем полученные от нулевого процессора элементы
					Polynom a = (Polynom) m1[0];
					Polynom b = (Polynom) m1[1];
					pred.paddEvent("процессор получил подполиномы от 0 : "
							+ " s1=" + a + " ;s2=" + b, "процессор " + myrank
							+ " начал вычисления");
					System.out.println("процессор " + myrank
							+ " принял свою часть =" + a.toString(ring)
							+ "    " + b.toString(ring));
					ab = a.multiply(b, ring);
					System.out.println("ab " + myrank + " = "
							+ ab.toString(ring));
				} else {
					;
				}
			}
			if (p < ness) {// если кол-во проц меньше чем нужно
				if (myrank < newp) {
					//!!!! MPI.COMM_WORLD.Recv(m1, 0, 2, //!!!! MPI.OBJECT, 0, 111);
					// перемножаем полученные от нулевого процессора элементы
					Polynom a = (Polynom) m1[0];
					Polynom b = (Polynom) m1[1];
					pred.paddEvent("процессор получил подполиномы от 0 : "
							+ " s1=" + a + " ;s2=" + b, "процессор " + myrank
							+ " начал вычисления");
					System.out.println("процессор " + myrank
							+ " принял свою часть =" + a.toString(ring)
							+ "    " + b.toString(ring));
					ab = a.multiply(b, ring);
					System.out.println("ab " + myrank + " = "
							+ ab.toString(ring));
				} else {
					;
				}
			}
			if (p == ness) {// кол-во проц равно необходимому
				// принимаем от 0 процессора свои части полиномов и записываем
				// их в массив m1
				//!!!! MPI.COMM_WORLD.Recv(m1, 0, 2, //!!!! MPI.OBJECT, 0, 111);
				// перемножаем полученные от нулевого процессора элементы
				Polynom a = (Polynom) m1[0];
				Polynom b = (Polynom) m1[1];
				pred.paddEvent("процессор получил подполиномы от 0 : " + " s1="
						+ a + " ;s2=" + b, "процессор " + myrank
						+ " начал вычисления");
				System.out.println("процессор " + myrank
						+ " принял свою часть =" + a.toString(ring) + "    "
						+ b.toString(ring));
				ab = a.multiply(b, ring);
				System.out.println("ab " + myrank + " = " + ab.toString(ring));
			}
		}

		// общая программа для всех процессоров
		int c;
		Object[] new1 = new Object[1];
		Object[] new3 = new Object[1];
		if (p > ness) {
			System.out.println("p>ness");
			if (myrank < ness) {
				for (int kp = ness; kp >= 2; kp = kp / 2) {
					if (kp == ness)
						c = 1;
					else
						c = 2;
					int kp1 = kp % 2;
					if (kp1 == 0) {// если работает четное кол-во процессоров
						for (int j = 0; j < kp; j++) {
							if (j % 2 == 0) {
								if (myrank == j * c) {
									//!!!! MPI.COMM_WORLD.Recv(new1, 0, 1, //!!!! MPI.OBJECT,
									//!!!!		(j + 1) * c, 444);
									System.out.println("проц " + myrank
											+ " получил от " + (j + 1) * c);
									Polynom s = (Polynom) new1[0];
									pred.paddEvent("процессор " + myrank
											+ "получил результат s=" + s
											+ " от" + (j + 1) * c,
											"происходит суммирование");
									ab = ab.add(s, ring);
								} else {
									pred.paddEvent(
											"процессор не участвует в вычислениях",
											"процессор свободен");
									System.out.println("проц " + myrank
											+ "свободен");
									continue;
								}
							} else {
								if (myrank == j * c) {
									Object[] new2 = new Object[] { ab };
									//!!!! MPI.COMM_WORLD.Send(new2, 0, 1, //!!!! MPI.OBJECT,
									//!!!!		(j - 1) * c, 444);
									pred.paddEvent("процессор " + myrank
											+ " отправил результат s=" + ab
											+ " к" + (j - 1) * c,
											"процессор свободен");
								} else {
									pred.paddEvent(
											"процессор не участвует в вычислениях",
											"процессор свободен");
									continue;
								}
							}
						}
					} else {// если работает нечетное кол-во процессоров
						for (int j = 0; j < kp; j++) {
							if (j % 2 == 0) {// четные
								if (myrank == j * c) {
									if (myrank == 0) {
										//!!!! MPI.COMM_WORLD.Recv(new1, 0, 1,
												//!!!! MPI.OBJECT, (j + 1) * c, 444);
										//!!!! MPI.COMM_WORLD.Recv(new3, 0, 1,
												//!!!! MPI.OBJECT, (kp - 1) * c, 555);
										// pred.paddEvent("процессор "+ myrank +
										// "получил результат от" + (j+1)*c +
										// " и от " + ((kp-1)*c),
										// "происходит суммирование");
										Polynom s = (Polynom) new1[0];
										Polynom d = (Polynom) new3[0];
										pred.paddEvent("процессор " + myrank
												+ "получил результат s=" + s
												+ " и d=" + d + " от" + (j + 1)
												* c, "происходит суммирование");
										ab = ab.add(d.add(s, ring), ring);
									} else {
										//!!!! MPI.COMM_WORLD.Recv(new1, 0, 1,
												//!!!! MPI.OBJECT, (j + 1) * c, 444);
										// pred.paddEvent("процессор "+ myrank +
										// "получил результат от" + (j+1)*c,
										// "происходит суммирование");
										Polynom s = (Polynom) new1[0];
										pred.paddEvent("процессор " + myrank
												+ "получил результат s=" + s
												+ " от" + (j + 1) * c,
												"происходит суммирование");
										ab = ab.add(s, ring);
									}
								} else {
									pred.paddEvent(
											"процессор не участвует в вычислениях",
											"процессор свободен");
									continue;
								}
							} else {// нечетные
								if (myrank == (kp - 1) * c) {
									Object[] new2 = new Object[] { ab };
									//!!!! MPI.COMM_WORLD.Send(new2, 0, 1, //!!!! MPI.OBJECT,
								//!!!!			0, 555);
									pred.paddEvent("процессор " + myrank
											+ " отправил результат s=" + ab
											+ " к 0", "процессор свободен");
								}
								// pred.paddEvent("процессор "+ myrank +
								// " отправил результат 0",
								// "процессор свободен");}
								if (myrank == j * c) {
									Object[] new2 = new Object[] { ab };
									//!!!! MPI.COMM_WORLD.Send(new2, 0, 1, //!!!! MPI.OBJECT,
								//!!!!			(j - 1) * c, 444);
									pred.paddEvent("процессор " + myrank
											+ " отправил результат s=" + ab
											+ " к" + (j - 1) * c,
											"процессор свободен");
								}
								// pred.paddEvent("процессор "+ myrank +
								// " отправил результат " + (j-1)*c,
								// "процессор свободен");}
								else {
									pred.paddEvent(
											"процессор не участвует в вычислениях",
											"процессор свободен");
									continue;
								}
							}
						}
					}
				}
				pred.generateDebugLog();
				{
					return ab;
				}
			} else {
				return new Polynom("0", ring);
			}
		}

		if (p < ness) {
			System.out.println("p<ness");
			if (myrank < newp) {
				for (int kp = newp; kp >= 2; kp = kp / 2) {
					if (kp == newp)
						c = 1;
					else
						c = 2;
					int kp1 = kp % 2;
					if (kp1 == 0) {// если работает четное кол-во процессоров
						for (int j = 0; j < kp; j++) {
							if (j % 2 == 0) {
								if (myrank == j * c) {
									//!!!! MPI.COMM_WORLD.Recv(new1, 0, 1, //!!!! MPI.OBJECT,
								//!!!!			(j + 1) * c, 444);
									// pred.paddEvent("процессор "+ myrank +
									// "получил результат от" + (j+1)*c,
									// "происходит суммирование");
									Polynom s = (Polynom) new1[0];
									pred.paddEvent("процессор " + myrank
											+ "получил результат s=" + s
											+ " от" + (j + 1) * c,
											"происходит суммирование");
									ab = ab.add(s, ring);
								} else {
									pred.paddEvent(
											"процессор не участвует в вычислениях",
											"процессор свободен");
									continue;
								}
							} else {
								if (myrank == j * c) {
									Object[] new2 = new Object[] { ab };
									//!!!! MPI.COMM_WORLD.Send(new2, 0, 1, //!!!! MPI.OBJECT,
								//!!!!			(j - 1) * c, 444);
									// pred.paddEvent("процессор "+ myrank +
									// " отправил результат " + (j-1)*c,
									// "процессор свободен");
									pred.paddEvent("процессор " + myrank
											+ " отправил результат s=" + ab
											+ " к" + (j - 1) * c,
											"процессор свободен");
								} else {
									pred.paddEvent(
											"процессор не участвует в вычислениях",
											"процессор свободен");
									continue;
								}
							}
						}
					} else {// если работает нечетное кол-во процессоров
						for (int j = 0; j < kp; j++) {
							if (j % 2 == 0) {// четные
								if (myrank == j * c) {
									if (myrank == 0) {
										//!!!! MPI.COMM_WORLD.Recv(new1, 0, 1,
												//!!!! MPI.OBJECT, (j + 1) * c, 444);
										//!!!! MPI.COMM_WORLD.Recv(new3, 0, 1,
												//!!!! MPI.OBJECT, (kp - 1) * c, 555);
										// pred.paddEvent("процессор получил результат от "
										// +(j+1)*c+ " и от " + (kp-1)*c,
										// "происходит суммирование");
										Polynom s = (Polynom) new1[0];
										Polynom d = (Polynom) new3[0];
										pred.paddEvent("процессор " + myrank
												+ "получил результат s=" + s
												+ " и d=" + d + " от" + (j + 1)
												* c, "происходит суммирование");
										ab = ab.add(d.add(s, ring), ring);
									} else {
										//!!!! MPI.COMM_WORLD.Recv(new1, 0, 1,
												//!!!! MPI.OBJECT, (j + 1) * c, 444);
										// pred.paddEvent("процессор "+ myrank +
										// "получил результат от" + (j+1)*c,
										// "происходит суммирование");
										Polynom s = (Polynom) new1[0];
										pred.paddEvent("процессор " + myrank
												+ "получил результат s=" + s
												+ " от" + (j + 1) * c,
												"происходит суммирование");
										ab = ab.add(s, ring);
									}
								} else {
									pred.paddEvent(
											"процессор не участвует в вычислениях",
											"процессор свободен");
									continue;
								}
							} else {// нечетные
								if (myrank == (kp - 1) * c) {
									Object[] new2 = new Object[] { ab };
									//!!!! MPI.COMM_WORLD.Send(new2, 0, 1, //!!!! MPI.OBJECT,
									//!!!!		0, 555);
									// pred.paddEvent("процессор "+ myrank +
									// " отправил результат 0",
									// "процессор свободен");
									pred.paddEvent("процессор " + myrank
											+ " отправил результат s=" + ab
											+ " к 0", "процессор свободен");
								}
								if (myrank == j * c) {
									Object[] new2 = new Object[] { ab };
									//!!!! MPI.COMM_WORLD.Send(new2, 0, 1, //!!!! MPI.OBJECT,
								//!!!!			(j - 1) * c, 444);
									// pred.paddEvent("процессор "+ myrank +
									// " отправил результат " + (j-1)*c,
									// "процессор свободен");
									pred.paddEvent("процессор " + myrank
											+ " отправил результат s=" + ab
											+ " к" + (j - 1) * c,
											"процессор свободен");
								} else {
									pred.paddEvent(
											"процессор не участвует в вычислениях",
											"процессор свободен");
									continue;
								}
							}
						}
					}
				}
				pred.generateDebugLog();
				{
					return ab;
				}
			} else {
				return new Polynom("0", ring);
			}
		} else {
			System.out.println("p=ness");
			for (int kp = p; kp >= 2; kp = kp / 2) {
				if (kp == p)
					c = 1;
				else
					c = 2;
				int kp1 = kp % 2;
				if (kp1 == 0) {// если работает четное кол-во процессоров
					for (int j = 0; j < kp; j++) {
						if (j % 2 == 0) {
							if (myrank == j * c) {
								//!!!! MPI.COMM_WORLD.Recv(new1, 0, 1, //!!!! MPI.OBJECT,
								//!!!!		(j + 1) * c, 444);
								// pred.paddEvent("процессор "+ myrank +
								// "получил результат от" + (j+1)*c,
								// "происходит суммирование");
								Polynom s = (Polynom) new1[0];
								pred.paddEvent("процессор " + myrank
										+ "получил результат s=" + s + " от"
										+ (j + 1) * c,
										"происходит суммирование");
								ab = ab.add(s, ring);
							} else {
								pred.paddEvent(
										"процессор не участвует в вычислениях",
										"процессор свободен");
								continue;
							}
						} else {
							if (myrank == j * c) {
								Object[] new2 = new Object[] { ab };
								//!!!! MPI.COMM_WORLD.Send(new2, 0, 1, //!!!! MPI.OBJECT,
								//!!!!		(j - 1) * c, 444);
								// pred.paddEvent("процессор "+ myrank +
								// " отправил результат " + (j-1)*c,
								// "процессор свободен");
								pred.paddEvent("процессор " + myrank
										+ " отправил результат s=" + ab + " к"
										+ (j - 1) * c, "процессор свободен");
							} else {
								pred.paddEvent(
										"процессор не участвует в вычислениях",
										"процессор свободен");
								continue;
							}
						}
					}
				} else {// если работает нечетное кол-во процессоров
					for (int j = 0; j < kp; j++) {
						if (j % 2 == 0) {// четные
							if (myrank == j * c) {
								if (myrank == 0) {
									//!!!! MPI.COMM_WORLD.Recv(new1, 0, 1, //!!!! MPI.OBJECT,
									//!!!!		(j + 1) * c, 444);
									//!!!! MPI.COMM_WORLD.Recv(new3, 0, 1, //!!!! MPI.OBJECT,
								//!!!!			(kp - 1) * c, 555);
									// pred.paddEvent("процессор получил результат от "
									// +(j+1)*c+ " и от " + (kp-1)*c,
									// "происходит суммирование");
									Polynom s = (Polynom) new1[0];
									Polynom d = (Polynom) new3[0];
									pred.paddEvent("процессор " + myrank
											+ "получил результат s=" + s
											+ "и d=" + d + " от" + (j + 1) * c,
											"происходит суммирование");
									ab = ab.add(d.add(s, ring), ring);
								} else {
									//!!!! MPI.COMM_WORLD.Recv(new1, 0, 1, //!!!! MPI.OBJECT,
								//!!!!			(j + 1) * c, 444);
									// pred.paddEvent("процессор "+ myrank +
									// "получил результат от" + (j+1)*c,
									// "происходит суммирование");
									Polynom s = (Polynom) new1[0];
									pred.paddEvent("процессор " + myrank
											+ "получил результат s=" + s
											+ " от" + (j + 1) * c,
											"происходит суммирование");
									ab = ab.add(s, ring);
								}
							} else {
								pred.paddEvent(
										"процессор не участвует в вычислениях",
										"процессор свободен");
								continue;
							}
						} else {// нечетные
							if (myrank == (kp - 1) * c) {
								Object[] new2 = new Object[] { ab };
								//!!!! MPI.COMM_WORLD.Send(new2, 0, 1, //!!!! MPI.OBJECT, 0,
								//!!!!		555);
								// pred.paddEvent("процессор "+ myrank +
								// " отправил результат 0",
								// "процессор свободен");
								pred.paddEvent("процессор " + myrank
										+ " отправил результат s=" + ab
										+ " к 0", "процессор свободен");
							}
							if (myrank == j * c) {
								Object[] new2 = new Object[] { ab };
								//!!!! MPI.COMM_WORLD.Send(new2, 0, 1, //!!!! MPI.OBJECT,
							//!!!!			(j - 1) * c, 444);
								// pred.paddEvent("процессор "+ myrank +
								// " отправил результат " + (j-1)*c,
								// "процессор свободен");
								pred.paddEvent("процессор " + myrank
										+ " отправил результат s=" + ab + " к"
										+ (j - 1) * c, "процессор свободен");
							} else {
								pred.paddEvent(
										"процессор не участвует в вычислениях",
										"процессор свободен");
								continue;
							}
						}
					}
				}
			}
			pred.generateDebugLog();
			{
				return ab;
			}
		}
	}
}
