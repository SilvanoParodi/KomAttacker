package com.inandio.komattacker.connector;

import com.inandio.komattacker.entities.activity.LapEffort;
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
import com.inandio.komattacker.entities.activity.Comment;
import com.inandio.komattacker.entities.activity.Photo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public interface JStrava {
    public Athlete getCurrentAthlete() throws IOException;
    public Athlete updateAthlete(HashMap optionalParameters) throws IOException;
    public Athlete findAthlete(int id) throws IOException;
    public List<SegmentEffort> findAthleteKOMs(int athleteId)throws IOException;
    public List<SegmentEffort> findAthleteKOMs(int athleteId,int page, int per_page)throws IOException;
    public List<Athlete> getCurrentAthleteFriends()throws IOException;
    public List<Athlete> getCurrentAthleteFriends(int page, int per_page)throws IOException;
    public List<Athlete> findAthleteFriends(int id)throws IOException;
    public List<Athlete> findAthleteFriends(int id, int page, int per_page)throws IOException;
    public List<Athlete> getCurrentAthleteFollowers()throws IOException;
    public List<Athlete> getCurrentAthleteFollowers(int page, int per_page)throws IOException;
    public List<Athlete> findAthleteFollowers(int id)throws IOException;
    public List<Athlete> findAthleteFollowers(int id, int page, int per_page)throws IOException;
    public List<Athlete> findAthleteBothFollowing(int id)throws IOException;
    public List<Athlete> findAthleteBothFollowing(int id, int page, int per_page)throws IOException;
    public StravaActivity createActivity(String name, String type, String start_date_local, int elapsed_time)throws IOException;
    public StravaActivity createActivity(String name, String type, String start_date_local, int elapsed_time, String description, float distance )throws IOException;
    public void deleteActivity(int activityId)throws IOException;
    public StravaActivity findActivity(int id)throws IOException;
    public StravaActivity findActivity(int id,boolean include_all_efforts)throws IOException;
    public StravaActivity updateActivity(int activityId, HashMap optionalParameters)throws IOException;
    public List<StravaActivity> getCurrentAthleteActivities()throws IOException;
    public List<StravaActivity> getCurrentAthleteActivities( int page, int per_page)throws IOException;
    public List<StravaActivity> getCurrentAthleteActivitiesBeforeDate( long before)throws IOException;
    public List<StravaActivity> getCurrentAthleteActivitiesAfterDate( long after)throws IOException;
    public List<StravaActivity> getCurrentFriendsActivities()throws IOException;
    public List<StravaActivity> getCurrentFriendsActivities( int page, int per_page)throws IOException;
    public List<Zone> getActivityZones (int activityId)throws IOException;
    public List<LapEffort> findActivityLaps(int activityId)throws IOException;
    public List<Comment> findActivityComments(int activityId)throws IOException;
    public List<Comment> findActivityComments(int activityId,boolean markdown, int page, int per_page)throws IOException;
    public List<Athlete> findActivityKudos(int activityId)throws IOException;
    public List<Athlete> findActivityKudos(int activityId,int page, int per_page)throws IOException;
    public List<Photo>findActivityPhotos(int activityId)throws IOException;
    public List<Athlete> findClubMembers(int clubId)throws IOException;
    public List<Athlete> findClubMembers(int clubId,int page, int per_page)throws IOException;
    public List<StravaActivity> findClubActivities(int clubId)throws IOException;
    public List<StravaActivity> findClubActivities(int clubId, int page, int per_page)throws IOException;
    public Club findClub(int id)throws IOException;
    public List<Club> getCurrentAthleteClubs()throws IOException;
    public Gear findGear(String id)throws IOException;
    public Segment findSegment(long segmentId)throws IOException;
    public List<Segment> getCurrentStarredSegment()throws IOException;
    public SegmentLeaderBoard findSegmentLeaderBoard (long segmentId)throws IOException;
    public SegmentLeaderBoard findSegmentLeaderBoard (long segmentId, int page, int per_page)throws IOException;
    public SegmentLeaderBoard findSegmentLeaderBoard (long segmentId, HashMap optionalParameters)throws IOException;
    public List<Segment>findSegments(Bound bound)throws IOException;
    public List<Segment>findSegments(Bound bound, int minimun_category, int maximun_category)throws IOException;
    public SegmentEffort findSegmentEffort(long id)throws IOException;
    public List<Stream>findActivityStreams(int activityId,String[]types)throws IOException;
    public List<Stream>findActivityStreams(int activityId,String[]types,String resolution, String series_type)throws IOException;
    public List<Stream>findEffortStreams(long id,String[]types)throws IOException;
    public List<Stream>findEffortStreams(long activityId,String[]types,String resolution,String series_type)throws IOException;
    public List<Stream>findSegmentStreams(int activityId,String[]types)throws IOException;
    public List<Stream>findSegmentStreams(int activityId,String[]types,String resolution,String series_type)throws IOException;
    public UploadStatus uploadActivity(String data_type,File file)throws IOException;
    public UploadStatus uploadActivity(String activity_type,String name,String description,int is_private,int trainer,String data_type,String external_id,File file)throws IOException;
    public UploadStatus checkUploadStatus(int uploadId)throws IOException;

}