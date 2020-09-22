package com.inandio.komattacker.connector;


import com.google.gson.Gson;
import com.inandio.komattacker.entities.activity.Comment;
import com.inandio.komattacker.entities.activity.LapEffort;
import com.inandio.komattacker.entities.activity.Photo;
import com.inandio.komattacker.entities.activity.StravaActivity;
import com.inandio.komattacker.entities.activity.UploadStatus;
import com.inandio.komattacker.entities.activity.Zone;
import com.inandio.komattacker.entities.athlete.Athlete;
import com.inandio.komattacker.entities.club.Club;
import com.inandio.komattacker.entities.gear.Gear;
import com.inandio.komattacker.entities.segment.Bound;
import com.inandio.komattacker.entities.segment.Segment;
import com.inandio.komattacker.entities.segment.SegmentEffort;
import com.inandio.komattacker.entities.segment.SegmentLeaderBoard;
import com.inandio.komattacker.entities.stream.Stream;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class JStravaV3 implements JStrava {

    private String accessToken;
    private Athlete currentAthlete;


    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public Athlete getCurrentAthlete() {
        return currentAthlete;
    }

    @Override
    public Athlete updateAthlete(HashMap optionalParameters) {
        String URL="https://www.strava.com/api/v3/athlete";
        String result=putResult(URL,optionalParameters);
        Gson gson= new Gson();
        Athlete athlete= gson.fromJson(result,Athlete.class);

        return athlete;
    }


    @Override
    public Athlete findAthlete(int id) throws IOException {

        String URL="https://www.strava.com/api/v3/athletes/"+id;
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete athlete= gson.fromJson(result,Athlete.class);

        return athlete;

    }



    @Override
    public List<SegmentEffort> findAthleteKOMs(int athleteId) throws IOException {
        String URL="https://www.strava.com/api/v3/athletes/"+athleteId+"/koms";
        String result=getResult(URL);
        Gson gson= new Gson();
        SegmentEffort[] segmentEffortArray=gson.fromJson(result,SegmentEffort[].class);
        List<SegmentEffort>segmentEfforts= Arrays.asList(segmentEffortArray);
        return segmentEfforts;
    }

    @Override
    public List<SegmentEffort> findAthleteKOMs(int athleteId,int page,int per_page) throws IOException {
        String URL="https://www.strava.com/api/v3/athletes/"+athleteId+"/koms?page="+page+"&per_page="+per_page;
        String result=getResult(URL);
        Gson gson= new Gson();
        SegmentEffort[] segmentEffortArray=gson.fromJson(result,SegmentEffort[].class);
        List<SegmentEffort>segmentEfforts= Arrays.asList(segmentEffortArray);
        return segmentEfforts;
    }

    @Override
    public List<Athlete> getCurrentAthleteFriends() throws IOException {
        String URL="https://www.strava.com/api/v3/athlete/friends";
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete[] athletesArray= gson.fromJson(result,Athlete[].class);


        List<Athlete>athletes= Arrays.asList(athletesArray);


        return athletes;
    }

    @Override
    public List<Athlete> getCurrentAthleteFriends(int page, int per_page) throws IOException {
        String URL="https://www.strava.com/api/v3/athlete/friends?page="+page+"&per_page="+per_page;
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete[] athletesArray= gson.fromJson(result,Athlete[].class);


        List<Athlete>athletes= Arrays.asList(athletesArray);


        return athletes;
    }


    @Override
    public List<Athlete> findAthleteFriends(int id) throws IOException{
        String URL="https://www.strava.com/api/v3/athletes/"+id+"/friends";
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete[] athletesArray= gson.fromJson(result,Athlete[].class);

        List<Athlete> athletes = null;
        if (athletesArray != null)
        {
            athletes = Arrays.asList(athletesArray);
        }

        return athletes;
    }

    @Override
    public List<Athlete> findAthleteFriends(int id,int page, int per_page) throws IOException {
        String URL="https://www.strava.com/api/v3/athletes/"+id+"/friends?page="+page+"&per_page="+per_page;
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete[] athletesArray= gson.fromJson(result,Athlete[].class);


        List<Athlete>athletes= Arrays.asList(athletesArray);


        return athletes;
    }

    @Override
    public List<Athlete> getCurrentAthleteFollowers() throws IOException {
        String URL="https://www.strava.com/api/v3/athlete/followers";
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete[] athletesArray= gson.fromJson(result,Athlete[].class);


        List<Athlete>athletes= Arrays.asList(athletesArray);


        return athletes;
    }

    @Override
    public List<Athlete> getCurrentAthleteFollowers(int page, int per_page) throws IOException {
        String URL="https://www.strava.com/api/v3/athlete/followers?page="+page+"&per_page="+per_page;
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete[] athletesArray= gson.fromJson(result,Athlete[].class);


        List<Athlete>athletes= Arrays.asList(athletesArray);


        return athletes;
    }


    @Override
    public List<Athlete> findAthleteFollowers(int id) throws IOException {
        String URL="https://www.strava.com/api/v3/athletes/"+id+"/followers";
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete[] athletesArray= gson.fromJson(result,Athlete[].class);


        List<Athlete>athletes= Arrays.asList(athletesArray);


        return athletes;
    }

    @Override
    public List<Athlete> findAthleteFollowers(int id,int page, int per_page) throws IOException {
        String URL="https://www.strava.com/api/v3/athletes/"+id+"/followers?page="+page+"&per_page="+per_page;
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete[] athletesArray= gson.fromJson(result,Athlete[].class);


        List<Athlete>athletes= Arrays.asList(athletesArray);


        return athletes;
    }


    @Override
    public List<Athlete> findAthleteBothFollowing(int id) throws IOException {
        String URL="https://www.strava.com/api/v3/athletes/"+id+"/both-following";
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete[] athletesArray= gson.fromJson(result,Athlete[].class);


        List<Athlete>athletes= Arrays.asList(athletesArray);


        return athletes;
    }

    @Override
    public List<Athlete> findAthleteBothFollowing(int id,int page, int per_page) throws IOException {
        String URL="https://www.strava.com/api/v3/athletes/"+id+"/both-following?page="+page+"&per_page="+per_page;
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete[] athletesArray= gson.fromJson(result,Athlete[].class);


        List<Athlete>athletes= Arrays.asList(athletesArray);


        return athletes;
    }

    @Override
    public StravaActivity createActivity(String name, String type, String start_date_local, int elapsed_time) {
        String URL="https://www.strava.com/api/v3/activities?name="+name+"&type="+type+"&start_date_local="+start_date_local+"&elapsed_time="+elapsed_time;
        String result=postResult(URL);
        Gson gson= new Gson();
        System.out.println("RESULTADO"+result);
        StravaActivity stravaActivity =gson.fromJson(result,StravaActivity.class);
        return stravaActivity;
    }

    @Override
    public StravaActivity createActivity(String name, String type, String start_date_local, int elapsed_time, String description, float distance) {
        String URL="https://www.strava.com/api/v3/activities?name="+name+"&type="+type+"&start_date_local="+start_date_local+"&elapsed_time="+elapsed_time+"&description="+description+"&distance="+distance;
        String result=postResult(URL);
        Gson gson= new Gson();
        StravaActivity stravaActivity =gson.fromJson(result,StravaActivity.class);
        return stravaActivity;
    }

    @Override
    public void deleteActivity(int activityId) {
        String URL="https://www.strava.com/api/v3/activities/"+activityId;
        String result=deleteResult(URL);
        Gson gson= new Gson();
        gson.fromJson(result,String.class);


    }

    @Override
    public StravaActivity findActivity(int id) throws IOException{
        String URL="https://www.strava.com/api/v3/activities/"+id;
        String result=getResult(URL);
        Gson gson= new Gson();
        StravaActivity stravaActivity = gson.fromJson(result,StravaActivity.class);

        return stravaActivity;
    }

    @Override
    public StravaActivity findActivity(int id,boolean include_all_efforts) throws IOException {
        String URL="https://www.strava.com/api/v3/activities/"+id+"?include_all_efforts="+include_all_efforts;
        String result=getResult(URL);
        Gson gson= new Gson();
        StravaActivity stravaActivity = gson.fromJson(result,StravaActivity.class);

        return stravaActivity;
    }

    @Override
    public StravaActivity updateActivity(int activityId, HashMap optionalParameters) {
        String URL="https://www.strava.com/api/v3/activities/"+activityId;
        String result=putResult(URL,optionalParameters);
        Gson gson= new Gson();
        StravaActivity stravaActivity = gson.fromJson(result,StravaActivity.class);

        return stravaActivity;
    }

    @Override
    public List<StravaActivity> getCurrentAthleteActivities() throws IOException {
        String URL="https://www.strava.com/api/v3/athlete/activities";
        String result=getResult(URL);
        Gson gson= new Gson();
        StravaActivity[] activitiesArray =gson.fromJson(result,StravaActivity[].class);
        List<StravaActivity>currentActivities= Arrays.asList(activitiesArray);
        return currentActivities;
    }

    @Override
    public List<StravaActivity> getCurrentAthleteActivities(int page,int per_page) throws IOException {
        String URL="https://www.strava.com/api/v3/athlete/activities?page="+page+"&per_page="+per_page;
        String result=getResult(URL);
        Gson gson= new Gson();
        StravaActivity[] activitiesArray =gson.fromJson(result,StravaActivity[].class);
        List<StravaActivity>currentActivities= Arrays.asList(activitiesArray);
        return currentActivities;
    }

    @Override
    public List<StravaActivity> getCurrentAthleteActivitiesBeforeDate(long before) throws IOException {
        String URL="https://www.strava.com/api/v3/athlete/activities?before="+before;
        String result=getResult(URL);
        Gson gson= new Gson();
        StravaActivity[] activitiesArray =gson.fromJson(result,StravaActivity[].class);
        List<StravaActivity>currentActivities= Arrays.asList(activitiesArray);
        return currentActivities;
    }

    @Override
    public List<StravaActivity> getCurrentAthleteActivitiesAfterDate(long after) throws IOException {
        String URL="https://www.strava.com/api/v3/athlete/activities?after="+after;
        String result=getResult(URL);
        Gson gson= new Gson();
        StravaActivity[] activitiesArray =gson.fromJson(result,StravaActivity[].class);
        List<StravaActivity>currentActivities= Arrays.asList(activitiesArray);
        return currentActivities;
    }


    public List<StravaActivity> getCurrentFriendsActivities() throws IOException {
        String URL="https://www.strava.com/api/v3/activities/following";
        String result=getResult(URL);
        Gson gson= new Gson();
        StravaActivity[] activitiesArray =gson.fromJson(result,StravaActivity[].class);
        List<StravaActivity>currentFriendsActivities= Arrays.asList(activitiesArray);
        return currentFriendsActivities;
    }

    public List<StravaActivity> getCurrentFriendsActivities(int page, int per_page) throws IOException{
        String URL="https://www.strava.com/api/v3/activities/following?page="+page+"&per_page="+per_page;
        String result=getResult(URL);
        Gson gson= new Gson();
        StravaActivity[] activitiesArray =gson.fromJson(result,StravaActivity[].class);
        List<StravaActivity>currentFriendsActivities= Arrays.asList(activitiesArray);
        return currentFriendsActivities;
    }

    @Override
    public List<Zone> getActivityZones(int activityId) throws IOException {
        String URL="https://www.strava.com/api/v3/activities/"+activityId+"/zones";
        String result=getResult(URL);
        Gson gson= new Gson();
        Zone[] zonesArray=gson.fromJson(result,Zone[].class);
        List<Zone> zones=Arrays.asList(zonesArray);
        return zones;
    }


    @Override
    public List<LapEffort> findActivityLaps(int activityId) throws IOException {
        String URL="https://www.strava.com/api/v3/activities/"+activityId+"/laps";
        String result=getResult(URL);
        Gson gson= new Gson();
        LapEffort[] lapEffortsArray =gson.fromJson(result,LapEffort[].class);
        List<LapEffort>lapEfforts= Arrays.asList(lapEffortsArray);
        return lapEfforts;
    }


    @Override
    public List<Comment> findActivityComments(int activityId) throws IOException {

        String URL="https://www.strava.com/api/v3/activities/"+activityId+"/comments";
        String result=getResult(URL);
        Gson gson= new Gson();
        Comment[] commentsArray= gson.fromJson(result,Comment[].class);


        List<Comment>comments= Arrays.asList(commentsArray);


        return comments;
    }

    @Override
    public List<Comment> findActivityComments(int activityId,boolean markdown, int page, int per_page) throws IOException {

        String URL="https://www.strava.com/api/v3/activities/"+activityId+"/comments?markdown="+markdown+"&page="+page+"&per_page="+per_page;
        String result=getResult(URL);
        Gson gson= new Gson();
        Comment[] commentsArray= gson.fromJson(result,Comment[].class);


        List<Comment>comments= Arrays.asList(commentsArray);


        return comments;
    }


    @Override
    public List<Athlete> findActivityKudos(int activityId) throws IOException {
        String URL="https://www.strava.com/api/v3/activities/"+activityId+"/kudos";
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete[] athletesArray= gson.fromJson(result,Athlete[].class);


        List<Athlete>athletes= Arrays.asList(athletesArray);


        return athletes;
    }

    @Override
    public List<Athlete> findActivityKudos(int activityId, int page, int per_page) throws IOException {
        String URL="https://www.strava.com/api/v3/activities/"+activityId+"/kudos?page="+page+"&per_page="+per_page;
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete[] athletesArray= gson.fromJson(result,Athlete[].class);


        List<Athlete>athletes= Arrays.asList(athletesArray);


        return athletes;
    }


    @Override
    public List<Athlete> findClubMembers(int clubId) throws IOException {
        String URL="https://www.strava.com/api/v3/clubs/"+clubId+"/members";
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete[] athletesArray= gson.fromJson(result,Athlete[].class);


        List<Athlete>athletes= Arrays.asList(athletesArray);


        return athletes;
    }

    @Override
    public List<Athlete> findClubMembers(int clubId,int page, int per_page) throws IOException {
        String URL="https://www.strava.com/api/v3/clubs/"+clubId+"/members?page="+page+"&per_page="+per_page;
        String result=getResult(URL);
        Gson gson= new Gson();
        Athlete[] athletesArray= gson.fromJson(result,Athlete[].class);


        List<Athlete>athletes= Arrays.asList(athletesArray);


        return athletes;
    }


    @Override
    public List<StravaActivity> findClubActivities(int clubId) throws IOException {
        String URL="https://www.strava.com/api/v3/clubs/"+clubId+"/activities";
        String result=getResult(URL);
        Gson gson= new Gson();
        StravaActivity[] activitiesArray =gson.fromJson(result,StravaActivity[].class);
        List<StravaActivity>clubActivities= Arrays.asList(activitiesArray);
        return clubActivities;
    }


    @Override
    public List<StravaActivity> findClubActivities(int clubId,int page, int per_page) throws IOException {
        String URL="https://www.strava.com/api/v3/clubs/"+clubId+"/activities"+"?page="+page+"&per_page="+per_page;
        String result=getResult(URL);
        Gson gson= new Gson();
        StravaActivity[] activitiesArray =gson.fromJson(result,StravaActivity[].class);
        List<StravaActivity>clubActivities= Arrays.asList(activitiesArray);
        return clubActivities;
    }

    @Override
    public Club findClub(int id) throws IOException {
        String URL="https://www.strava.com/api/v3/clubs/"+id;
        String result=getResult(URL);
        Gson gson= new Gson();
        Club club= gson.fromJson(result,Club.class);

        return club;
    }



    public List<Club> getCurrentAthleteClubs() throws IOException {
        String URL="https://www.strava.com/api/v3/athlete/clubs";
        String result=getResult(URL);
        Gson gson= new Gson();
        Club[] clubsArray= gson.fromJson(result,Club[].class);


        List<Club>clubs= Arrays.asList(clubsArray);


        return clubs;
    }


    @Override
    public Gear findGear(String id) throws IOException {
        String URL="https://www.strava.com/api/v3/gear/"+id;
        String result=getResult(URL);
        Gson gson= new Gson();
        Gear gear= gson.fromJson(result,Gear.class);

        return gear;
    }

    @Override
    public Segment findSegment(long segmentId) throws IOException {
        String URL="https://www.strava.com/api/v3/segments/"+segmentId;
        String result=getResult(URL);
        Gson gson= new Gson();
        Segment segment= gson.fromJson(result,Segment.class);
        return segment;
    }


    public List<Segment> getCurrentStarredSegment() throws IOException {
        String URL="https://www.strava.com/api/v3/segments/starred";
        String result=getResult(URL);
        Gson gson= new Gson();
        Segment[] segmentsArray= gson.fromJson(result,Segment[].class);


        List<Segment>segments= Arrays.asList(segmentsArray);


        return segments;
    }





    @Override
    public List<Photo> findActivityPhotos(int activityId) throws IOException {

        String URL="https://www.strava.com/api/v3/activities/"+activityId+"/photos";
        String result=getResult(URL);
        Gson gson= new Gson();
        Photo[] photosArray =gson.fromJson(result,Photo[].class);
        List<Photo>photos= Arrays.asList(photosArray);
        return photos;
    }

    @Override
    public SegmentLeaderBoard findSegmentLeaderBoard(long segmentId) throws IOException {
        String URL="https://www.strava.com/api/v3/segments/"+segmentId+"/leaderboard";
        String result=getResult(URL);
        Gson gson= new Gson();
        SegmentLeaderBoard segmentLeaderBoard= gson.fromJson(result,SegmentLeaderBoard.class);
        return segmentLeaderBoard;
    }


    @Override
    public SegmentLeaderBoard findSegmentLeaderBoard(long segmentId, int page, int per_page) throws IOException {
        String URL="https://www.strava.com/api/v3/segments/"+segmentId+"/leaderboard?page="+page+"&per_page="+per_page;
        String result=getResult(URL);
        Gson gson= new Gson();
        SegmentLeaderBoard segmentLeaderBoard= gson.fromJson(result,SegmentLeaderBoard.class);
        return segmentLeaderBoard;
    }



    @Override
    public SegmentLeaderBoard findSegmentLeaderBoard(long segmentId, HashMap optionalParameters) {
        String URL="https://www.strava.com/api/v3/segments/"+segmentId+"/leaderboard";
        String result=getResult(URL,optionalParameters);
        Gson gson= new Gson();
        SegmentLeaderBoard segmentLeaderBoard= gson.fromJson(result,SegmentLeaderBoard.class);
        return segmentLeaderBoard;
    }





    @Override
    public List<Segment> findSegments(Bound bound) throws IOException {
        String URL="https://www.strava.com/api/v3/segments/explore?bounds="+bound.toString();
        String result=getResult(URL);


        //////////UGLY HACK TO ALLOW GSON TO PARSE THE JSON STRING AND RETURN A LIST OF SEGMENTS

        String segmentString="\\{\"segments\":";

            result=result.replaceFirst(segmentString, "");
            result=result.substring(0,result.lastIndexOf("}"));

        Gson gson= new Gson();
        Segment[]segmentsArray= gson.fromJson(result,Segment[].class);
        List<Segment> segments=Arrays.asList(segmentsArray);
        return segments;
    }

    @Override
    public List<Segment> findSegments(Bound bounds, int minimun_category, int maximun_category) throws IOException {
        String URL="https://www.strava.com/api/v3/segments/explore?bounds="+bounds.toString()+"&min_cat=" + minimun_category +"&mac_cat=" + maximun_category;
        String result=getResult(URL);

        //////////UGLY HACK TO ALLOW GSON TO PARSE THE JSON STRING AND RETURN A LIST OF SEGMENTS

        String segmentString="\\{\"segments\":";

        result=result.replaceFirst(segmentString, "");
        result=result.substring(0,result.lastIndexOf("}"));


        Gson gson= new Gson();
        Segment[]segmentsArray= gson.fromJson(result,Segment[].class);
        List<Segment> segments=Arrays.asList(segmentsArray);
        return segments;
    }




    @Override
    public SegmentEffort findSegmentEffort(long id) throws IOException {
        String URL="https://www.strava.com/api/v3/segment_efforts/"+id;
        String result=getResult(URL);
        Gson gson= new Gson();
        SegmentEffort segmentEffort = gson.fromJson(result,SegmentEffort.class);
        return segmentEffort;
    }


    @Override
    public List<Stream> findActivityStreams(int activityId, String[] types) throws IOException {

        StringBuilder builder= new StringBuilder();

        for (int i=0;i<types.length;i++)
        {
            if (i!=0)
            {
                builder.append(",");
            }
            builder.append(types[i]);
        }

        String URL="https://www.strava.com/api/v3/activities/"+activityId+"/streams/"+builder.toString();
        String result= getResult(URL);
        Gson gson= new Gson();
        Stream[] streamsArray=gson.fromJson(result,Stream[].class);
        List<Stream> streams=Arrays.asList(streamsArray);
        return streams;

    }

    @Override
    public List<Stream> findActivityStreams(int activityId, String[] types, String resolution, String series_type) throws IOException{
        StringBuilder builder= new StringBuilder();

        for (int i=0;i<types.length;i++)
        {
            if (i!=0)
            {
                builder.append(",");
            }
            builder.append(types[i]);
        }

        String URL="https://www.strava.com/api/v3/activities/"+activityId+"/streams/"+builder.toString()+"?resolution="+resolution;

        if (series_type!=null && !series_type.isEmpty())
        {
            URL+="&series_type="+series_type;
        }

        String result= getResult(URL);
        Gson gson= new Gson();
        Stream[] streamsArray=gson.fromJson(result,Stream[].class);
        List<Stream> streams=Arrays.asList(streamsArray);
        return streams;
    }


    @Override
    public List<Stream> findEffortStreams(long id, String[] types) throws IOException{
        StringBuilder builder= new StringBuilder();

        for (int i=0;i<types.length;i++)
        {
            if (i!=0)
            {
                builder.append(",");
            }
            builder.append(types[i]);
        }

        String URL="https://www.strava.com/api/v3/segment_efforts/"+id+"/streams/"+builder.toString();
        String result= getResult(URL);
        Gson gson= new Gson();
        Stream[] streamsArray=gson.fromJson(result,Stream[].class);
        List<Stream> streams=Arrays.asList(streamsArray);
        return streams;
    }

    @Override
    public List<Stream> findEffortStreams(long id, String[] types, String resolution, String series_type) throws IOException{
        StringBuilder builder= new StringBuilder();

        for (int i=0;i<types.length;i++)
        {
            if (i!=0)
            {
                builder.append(",");
            }
            builder.append(types[i]);
        }

        String URL="https://www.strava.com/api/v3/segment_efforts/"+id+"/streams/"+builder.toString()+"?resolution="+resolution;

        if (series_type!=null && !series_type.isEmpty())
        {
            URL+="&series_type="+series_type;
        }

        String result= getResult(URL);
        Gson gson= new Gson();
        Stream[] streamsArray=gson.fromJson(result,Stream[].class);
        List<Stream> streams=Arrays.asList(streamsArray);
        return streams;
    }

    @Override
    public List<Stream> findSegmentStreams(int id, String[] types) throws IOException {
        StringBuilder builder= new StringBuilder();

        for (int i=0;i<types.length;i++)
        {
            if (i!=0)
            {
                builder.append(",");
            }
            builder.append(types[i]);
        }

        String URL="https://www.strava.com/api/v3/segments/"+id+"/streams/"+builder.toString();
        String result= getResult(URL);
        Gson gson= new Gson();
        Stream[] streamsArray=gson.fromJson(result,Stream[].class);
        List<Stream> streams=Arrays.asList(streamsArray);
        return streams;
    }

    @Override
    public List<Stream> findSegmentStreams(int id, String[] types, String resolution, String series_type) throws IOException {
        StringBuilder builder= new StringBuilder();

        for (int i=0;i<types.length;i++)
        {
            if (i!=0)
            {
                builder.append(",");
            }
            builder.append(types[i]);
        }

        String URL="https://www.strava.com/api/v3/segments/"+id+"/streams/"+builder.toString()+"?resolution="+resolution;

        if (series_type!=null && !series_type.isEmpty())
        {
            URL+="&series_type="+series_type;
        }

        String result= getResult(URL);
        Gson gson= new Gson();
        Stream[] streamsArray=gson.fromJson(result,Stream[].class);
        List<Stream> streams=Arrays.asList(streamsArray);
        return streams;
    }

    @Override
    public UploadStatus uploadActivity(String data_type, File file) {
        return null;
    }

    @Override
    public UploadStatus uploadActivity(String activity_type, String name, String description, int is_private, int trainer, String data_type, String external_id, File file) {
        return null;
    }

    @Override
    public UploadStatus checkUploadStatus(int uploadId) throws IOException {

        String URL="https://www.strava.com/api/v3/uploads/"+uploadId;
        String result= getResult(URL);
        Gson gson= new Gson();
        UploadStatus status=gson.fromJson(result,UploadStatus.class);

        return status;
    }


    public JStravaV3(String access_token) throws IOException{
        this.accessToken = access_token;
        String URL="https://www.strava.com/api/v3/athlete";
        String result=getResult(URL);
        Gson gson= new Gson();
        currentAthlete =gson.fromJson(result,Athlete.class);

    }



    private String getResult(String URL) throws IOException {
        StringBuilder sb= new StringBuilder();

        try {
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization","Bearer "+getAccessToken());
        //    conn.setRequestProperty("per_page","1");
         //   conn.setRequestProperty("access_token", getAccessToken());


            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode() + " - " + conn.getResponseMessage());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            conn.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
            throw  e;
        }

        return sb.toString();

    }





    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }



    private String getResult(String URL, HashMap optionalParameters){
        StringBuilder sb= new StringBuilder();
        sb.append(URL);
        try {

            Iterator iterator= optionalParameters.keySet().iterator();

            int index=0;
            while(iterator.hasNext())
            {
                if (index==0)
                {
                    sb.append("?");
                }
                else
                {
                    sb.append("&");
                }
                String key=(String)iterator.next();
                sb.append(key);
                sb.append("=");
                sb.append(URLEncoder.encode(optionalParameters.get(key).toString(), "UTF-8"));
                index++;
            }



                URI uri = new URI(String.format(sb.toString()));
                URL url= uri.toURL();


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization","Bearer "+getAccessToken());
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode() + " - " + conn.getResponseMessage());
            }


            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            sb=new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            conn.disconnect();

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();

    }


    private String postResult(String URL){
        StringBuffer sb= new StringBuffer();
        try {

            String finalUrl="";

                String[] parsedUrl=URL.split("\\?");
                String params=URLEncoder.encode(parsedUrl[1], "UTF-8").replace("%3D","=").replace("%26","&");

            URL url= new URL(parsedUrl[0]+"?"+params);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization","Bearer "+getAccessToken());

           conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(params);
            wr.flush();
            wr.close();



            boolean redirect = false;
            // normally, 3xx is redirect
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }


            if (redirect) {

                // get redirect url from "location" header field
                String newUrl = conn.getHeaderField("Location");


                // open the new connnection again
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization","Bearer "+getAccessToken());



            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }







    private String putResult(String URL){
        StringBuilder sb= new StringBuilder();

        try {
            String finalUrl="";
            if (URL.contains("?"))
            {
                String[] parsedUrl=URL.split("\\?");
                String params=URLEncoder.encode(parsedUrl[1], "UTF-8");
                finalUrl=parsedUrl[0]+"?"+params;
            }
            else
            {
                finalUrl=URL;
            }

            URL url= new URL(finalUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization","Bearer "+getAccessToken());
            if (conn.getResponseCode() != 200 | conn.getResponseCode() != 201 ) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode() + " - " + conn.getResponseMessage());
            }


            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            sb=new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            conn.disconnect();

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }

        return sb.toString();

    }




    private String putResult(String URL, HashMap optionalParameters){
        StringBuilder sb= new StringBuilder();
        sb.append(URL);
        try {

            Iterator iterator= optionalParameters.keySet().iterator();

            int index=0;
            while(iterator.hasNext())
            {
                if (index==0)
                {
                    sb.append("?");
                }
                else
                {
                    sb.append("&");
                }
                String key=(String)iterator.next();
                sb.append(key);
                sb.append("=");
                sb.append(URLEncoder.encode(optionalParameters.get(key).toString(), "UTF-8"));
                index++;
            }

            URI uri = new URI(sb.toString());
            URL url= uri.toURL();



            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization","Bearer "+getAccessToken());
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode() + " - " + conn.getResponseMessage());
            }


            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            sb=new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            conn.disconnect();

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();

    }


    private String deleteResult(String URL){
        StringBuilder sb= new StringBuilder();
        sb.append(URL);
        try {


            URI uri = new URI(String.format(sb.toString()));
            URL url= uri.toURL();



            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization","Bearer "+getAccessToken());
            if (conn.getResponseCode() != 204) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode() + " - " + conn.getResponseMessage());
            }


            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            sb=new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            conn.disconnect();

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();

    }




}
