package org.explorer.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	private static Logger logger = LoggerFactory.getLogger(Utils.class);

    private static String OS = System.getProperty("os.name").toLowerCase();

	private static Utils instance = new Utils();
	
	public static final Utils getInstance() {
		return instance;
	}

	public <T> T find(T[] a, T f) {
		for (T s : a) {
			if (s.equals(f)) {
				return f;
			}
		}
		return null;
	}

	public String[] parseUriParameters(String href) {
		ArrayList<String> result = new ArrayList<>();
		Pattern p = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL);
		Matcher matcher = p.matcher(href);
		while (matcher.find()) {
			result.add(matcher.group(1));
			// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>found
			// match:"+matcher.group(1));
		}
		String[] res = new String[result.size()];
		result.toArray(res);
		return res;
	}

	public boolean in(String[] a, String f) {
		return (find(a, f) != null);
	}

	public class CastBuilder {

		private Object object;

		public CastBuilder(Object object) {
			this.object = object;
		}

		public <T> T as(Class<T> clazz) throws ClassCastException {
			return clazz.cast(object);
		}

		public <T> boolean is(Class<T> clazz) {
			return clazz.isInstance(object);
		}
		
		public <T> List<T> asListOf(Class<T> clazz) {
			List<T> list = new ArrayList<>();
			List<?> origin = (List<?>) object;
			for (Object o : origin) {
				list.add(clazz.cast(o));
			}
			return list;
		}

		public <K, V> Map<K, V> asMapOf(Class<K> class1, Class<V> class2) {
			Map<K, V> map = new HashMap<>();
			Map<?, ?> origin = (Map<?, ?>) object;
			Set<?> keys = origin.keySet();
			for (Object k : keys) {
				map.put(class1.cast(k), class2.cast(origin.get(k)));
			}
			return map;
		}

	}

	public CastBuilder object(Object object) {
		return instance.new CastBuilder(object);
	}

	public String toString(InputStream inputStream) throws IOException {
		Scanner scanner = new Scanner(inputStream);
		
		scanner.useDelimiter("\\A");
		String result = scanner.hasNext() ? scanner.next() : "";
		
		scanner.close();
		inputStream.close();

		return result;
	}

    public boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }

    public boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }
    public String getOS(){
        if (isWindows()) {
            return "win";
        } else if (isMac()) {
            return "osx";
        } else if (isUnix()) {
            return "uni";
        } else if (isSolaris()) {
            return "sol";
        } else {
            return "err";
        }
    }

}
