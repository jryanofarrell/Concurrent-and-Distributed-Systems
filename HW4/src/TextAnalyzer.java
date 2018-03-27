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
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import java.io.IOException;
// Do not change the signature of this class
public class TextAnalyzer extends Configured implements Tool {
    static HashMap<String,Integer> map = new HashMap<String,Integer>();

    // Replace "?" with your own output key / value types
    // The four template data types are:
    //     <Input Key Type, Input Value Type, Output Key Type, Output Value Type>
    public static class TextMapper extends Mapper<LongWritable, Text, Text, Text> {
        private Text word = new Text();
        public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of you mapper function
            StringTokenizer itr = new StringTokenizer(value.toString().replaceAll("[^A-Za-z0-9]", "").toLowerCase());
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                if(map.containsKey(word.toString())) {
                    System.out.println("yes! - " + map.get(word.toString()));
                    int count = map.get(word.toString());
                    map.put(word.toString(), count + 1);
                } else {
                    map.put(word.toString(), 1);
                    System.out.println("no! - adding " + word.toString());
                }
            }
        }
    }

    // Replace "?" with your own key / value types
    // NOTE: combiner's output key / value types have to be the same as those of mapper
    public static class TextCombiner extends Reducer<LongWritable, Text, Text, Text> {
        public void reduce(Text key, Iterable<Tuple> tuples, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of you combiner function
        }
    }

    // Replace "?" with your own input key / value types, i.e., the output
    // key / value types of your mapper function
    public static class TextReducer extends Reducer<Text, Text, Text, Text> {
        private final static Text emptyText = new Text("");
        private static Text queryWordText = new Text("");
        public void reduce(Text key, Iterable<Tuple> queryTuples, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of you reducer function

            // Write out the results; you may change the following example
            // code to fit with your reducer function.
            //   Write out the current context key
            context.write(key, emptyText);
            System.out.println("IN MAP REDUCE");
            for(String q : map.keySet()) {
                System.out.println("IN MAP REDUCE");
            }
            //   Write out query words and their count
            for(String queryWord: map.keySet()){
                String count = map.get(queryWord).toString() + ">";
                queryWordText.set("<" + queryWord + ",");
                context.write(queryWordText, new Text(count));
                System.out.println(queryWord + " " + count);
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
        // job.setCombinerClass(TextCombiner.class);
        job.setReducerClass(TextReducer.class);

        // Specify key / value types (Don't change them for the purpose of this assignment)
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        //   If your mapper and combiner's  output types are different from Text.class,
        //   then uncomment the following lines to specify the data types.
        //job.setMapOutputKeyClass(?.class);
        //job.setMapOutputValueClass(?.class);

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

    public static class Tuple {
        String contextWord;
        HashMap<String, Integer> queryMap = new HashMap<String, Integer>();

        public Tuple(String cw) {
            contextWord = cw;
        }
    }
}



