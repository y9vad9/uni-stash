package com.mathpar.parallel.utils.parallel_debugger;
import java.io.Serializable;
/**
 * Класс представляет одно событие для
 * отладки параллельного кода на процессоре
 */
public class DebugEvent implements Serializable, Comparable<DebugEvent> {
	private static final long serialVersionUID = -7584366799587558949L;
	private String label, event;
	private int proc;
	public String getEvent() {return event;	}
	public String getLabel() {return label;	}
	public int    getProc()  {return proc; }
	public long   getTime()  {return time; }
	/** временная метка данного события */
	private long time;
	/**
	 * Конструктор принимает метку события, текст события, номер процессора, на
	 * котором произошло событие и время события	 */
	public DebugEvent(String label, String event, int proc, long time) {
		this.label = label;
		this.event = event;
		this.time = time;
		this.proc = proc;
	}
	/**
	 * Метод сравнения двух событий по временным меткам */
	public int compareTo(DebugEvent o) {
		if (this.time < o.time) {return -1;}
		if (this.time > o.time) {return 1;}
		return 0;
	}
	/**
	 * Метод представления события в виде строки таблицы <table> в html */
	@Override
	public String toString() {
		return "<tr><td>" + getTime() + "</td><td> " + getProc() + "</td><td> "
				+ getLabel() + "</td><td> " + getEvent() + "</td></tr>";
	}
	public String toString(String style) {
		return "<tr style=\"" + style + "\"><td>" + getTime() + "</td><td> "
				+ getProc() + "</td><td> " + getLabel() + "</td><td> "
				+ getEvent() + "</td></tr>";
	}
}