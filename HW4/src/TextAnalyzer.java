import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.*;

// Do not change the signature of this class
public class TextAnalyzer extends Configured implements Tool {
    // Replace "?" with your own output key / value types
    // The four template data types are:
    //     <Input Key Type, Input Value Type, Output Key Type, Output Value Type>
    public static class TextMapper extends Mapper<LongWritable, Text, Text, Tuple> {
        private Text word = new Text();
        public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of you mapper function
            StringTokenizer itr = new StringTokenizer(value.toString().replaceAll("[^A-Za-z0-9]", " ").toLowerCase());
            HashMap<String, Integer> sentenceMap = new HashMap<String, Integer>();
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                if(sentenceMap.containsKey(word.toString())) {
                    int count = sentenceMap.get(word.toString());
                    sentenceMap.put(word.toString(), count + 1);
                } else {
                    sentenceMap.put(word.toString(), 1);
                }
            }
            for(String k : sentenceMap.keySet()) {
                if(sentenceMap.get(k) == 1) {
                    //context word, need to write Tuples
                    for(String k2 : sentenceMap.keySet()) {
                        //loop through sentence words again and write new tuple
                        if(!k.equals(k2)) {
                            context.write(new Text(k), new Tuple(new Text(k2), new IntWritable(sentenceMap.get(k2))));
                        }
                    }
                }
            }
        }
    }

    // Replace "?" with your own key / value types
    // NOTE: combiner's output key / value types have to be the same as those of mapper
    public static class TextCombiner extends Reducer<LongWritable, Text, Text, Tuple> {
        public void reduce(Text key, Iterable<Tuple> tuples, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of you combiner function
            HashSet<String> wordsUsed = new HashSet<String>();
            for(Tuple t : tuples) {
                if(!wordsUsed.contains(t.queryWord.toString())) {
                    wordsUsed.add(t.queryWord.toString());
                    int totalSum = t.count.get();
                    for(Tuple t2 : tuples) {
                        if(t2.queryWord.equals(t.queryWord.toString()) && t!=t2) {
                            totalSum += t2.count.get();
                        }
                    }
                    context.write(key, new Tuple(t.queryWord, new IntWritable(totalSum)));
                }
            }
        }
    }

    // Replace "?" with your own input key / value types, i.e., the output
    // key / value types of your mapper function
    public static class TextReducer extends Reducer<Text, Tuple, Text, Text> {
        private final static Text emptyText = new Text("");
        private static Text queryWordText = new Text("");
        public void reduce(Text key, Iterable<Tuple> queryTuples, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of you reducer function
            
            HashMap<String, Integer> queryMap = new HashMap<String, Integer>();
            for(Tuple t : queryTuples) {
                if(!queryMap.containsKey(t.queryWord.toString())) {
                    queryMap.put(t.queryWord.toString(), t.count.get());
                } else {
                    queryMap.put(t.queryWord.toString(), queryMap.get(t.queryWord.toString()).intValue()+t.count.get());
                }
            }

            Object[] sortedTuples = queryMap.entrySet().toArray();
            Arrays.sort(sortedTuples, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Map.Entry<String, Integer>) o2).getValue()
                               .compareTo(((Map.Entry<String, Integer>) o1).getValue());
                }
            });

            // Write out the results; you may change the following example
            // code to fit with your reducer function.
            //   Write out the current context key
            context.write(new Text("context word is "+key.toString()), emptyText);
            //   Write out query words and their count
            /*for(String queryWord: map.keySet()){
                String count = map.get(queryWord).toString() + ">";
                queryWordText.set("<" + queryWord + ",");
                context.write(queryWordText, new Text(count));
                System.out.println(queryWord + " " + count);
            }*/
            for (Object o : sortedTuples) {
                String count = ((Map.Entry<String, Integer>) o).getValue() + ">";
                queryWordText.set("<" + ((Map.Entry<String, Integer>) o).getKey() + ",");
                context.write(queryWordText, new Text(count));
            }
            //   Empty line for ending the current context key
            context.write(emptyText, emptyText);
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();

        // Create job
        Job job = new Job(conf, "mac7865_jro769"); // Replace with your EIDs
        job.setJarByClass(TextAnalyzer.class);

        // Setup MapReduce job
        job.setMapperClass(TextMapper.class);
        //   Uncomment the following line if you want to use Combiner class
        //job.setCombinerClass(TextCombiner.class);
        job.setReducerClass(TextReducer.class);

        // Specify key / value types (Don't change them for the purpose of this assignment)
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        //   If your mapper and combiner's  output types are different from Text.class,
        //   then uncomment the following lines to specify the data types.
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Tuple.class);

        // Input
        FileInputFormat.addInputPath(job, new Path(args[0]));
        job.setInputFormatClass(TextInputFormat.class);

        // Output
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setOutputFormatClass(TextOutputFormat.class);

        // Execute job and return status
        return job.waitForCompletion(true) ? 0 : 1;
    }

    // Do not modify the main method
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new TextAnalyzer(), args);
        System.exit(res);
    }

    public static class Tuple implements WritableComparable<Tuple> {
        Text queryWord;
        IntWritable count;
        
        //Default Constructor
        public Tuple() {
            queryWord = new Text();
            count = new IntWritable();
        }

        //Custom Constructor
        public Tuple(Text qw, IntWritable cnt) {
            queryWord = qw;
            count = cnt;
        }
        
        //Setter method to set the values of Tuple object
        public void set(Text qw, IntWritable cnt) {
            queryWord = qw;
            count = cnt;
        }     

        @Override
        //overriding default readFields method. 
        //It de-serializes the byte stream data
        public void readFields(DataInput in) throws IOException {
            queryWord.readFields(in);
            count.readFields(in);
        }    

        @Override
        //It serializes object data into byte stream data
        public void write(DataOutput out) throws IOException 
        {
            queryWord.write(out);
            count.write(out);
        }       

        @Override
        public int compareTo(Tuple o) 
        {
            return queryWord.compareTo(o.queryWord);
        }

        @Override
        public boolean equals(Object o) 
        {
            if (o instanceof Tuple) 
            {
                Tuple other = (Tuple) o;
                return queryWord.equals(other.queryWord);
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return queryWord.hashCode();
        }
    }
}



